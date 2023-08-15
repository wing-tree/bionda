package wing.tree.bionda.view.model

import android.app.Application
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import wing.tree.bionda.data.extension.eight
import wing.tree.bionda.data.extension.fiveSecondsInMilliseconds
import wing.tree.bionda.data.extension.ifTrue
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.extension.long
import wing.tree.bionda.data.extension.negativeOne
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.model.Result
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.provider.LocationProvider
import wing.tree.bionda.data.repository.ForecastRepository
import wing.tree.bionda.data.repository.NoticeRepository
import wing.tree.bionda.exception.PermissionsDeniedException
import wing.tree.bionda.extension.checkSelfPermission
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.model.Address
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.view.state.ForecastState
import wing.tree.bionda.view.state.MainState
import wing.tree.bionda.view.state.NoticeState
import wing.tree.bionda.view.state.RequestPermissionsState
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val alarmScheduler: AlarmScheduler,
    private val forecastRepository: ForecastRepository,
    private val locationProvider: LocationProvider,
    private val noticeRepository: NoticeRepository
) : AndroidViewModel(application) {
    private val location = MutableStateFlow<Result<Location?>>(Result.Loading)
    private val stopTimeoutMillis = Long.fiveSecondsInMilliseconds
    private val forecastState = location.map {
        when (it) {
            Result.Loading -> ForecastState.Loading
            is Complete.Success -> it.data?.let { location ->
                val (nx, ny) = location.toCoordinate()
                val address = getAddress(location)
                val forecast = forecastRepository.get(
                    nx = nx,
                    ny = ny
                )

                when (forecast) {
                    Result.Loading -> ForecastState.Loading
                    is Complete -> when (forecast) {
                        is Complete.Success -> ForecastState.Content(
                            address = address,
                            forecast = Forecast.toPresentationModel(forecast.data)
                        )

                        is Complete.Failure -> ForecastState.Error(forecast.throwable)
                    }
                }
            }
                ?: ForecastState.Error(NullPointerException())

            is Complete.Failure -> ForecastState.Error(it.throwable)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
        initialValue = ForecastState.initialValue
    )

    val inSelectionMode = MutableStateFlow(false)
    val selected = MutableStateFlow<ImmutableSet<Long>>(persistentSetOf())

    init {
        viewModelScope.launch {
            inSelectionMode.collectLatest {
                if (it.not()) {
                    delay(DefaultDurationMillis.long)

                    selected.value = persistentSetOf()
                }
            }
        }

        viewModelScope.launch {
            selected.collectLatest {
                delay(DefaultDurationMillis.long)

                inSelectionMode.value = it.isNotEmpty()
            }
        }
    }

    private val noticeState = combine(
        noticeRepository.load(),
        selected
    ) { notice, selected ->
        when (notice) {
            is Complete.Success -> {
                NoticeState.Content(
                    notices = notice.data,
                    selected = selected
                )
            }

            is Complete.Failure -> NoticeState.Error(notice.throwable)
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
            initialValue = NoticeState.initialValue
        )

    private val requestPermissionsState = MutableStateFlow(RequestPermissionsState.initialValue)

    val state: StateFlow<MainState> = combine(
        forecastState,
        inSelectionMode,
        noticeState,
        requestPermissionsState
    ) { forecastState, inSelectionMode, noticeState, requestPermissionsState ->
        MainState(
            forecastState = forecastState,
            inSelectionMode = inSelectionMode,
            noticeState = noticeState,
            requestPermissionsState = requestPermissionsState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
        initialValue = MainState()
    )

    private suspend fun getAddress(location: Location) = suspendCancellableCoroutine { cancellableContinuation ->
        val geocoder = Geocoder(getApplication(), Locale.KOREA)
        val maxResults = Int.eight

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.latitude, location.longitude, maxResults) { geocode ->
                val thoroughfare = geocode.firstOrNull {
                    it.thoroughfare.isNotNull()
                }
                    ?.thoroughfare

                thoroughfare?.let {
                    cancellableContinuation.resume(Address(thoroughfare = it))
                }
                    ?: cancellableContinuation.resume(null)
            }
        } else {
            @Suppress("DEPRECATION")
            geocoder.getFromLocation(location.latitude, location.longitude, maxResults)?.let { geocode ->
                val thoroughfare = geocode.firstOrNull {
                    it.thoroughfare.isNotNull()
                }
                    ?.thoroughfare

                thoroughfare?.let {
                    cancellableContinuation.resume(Address(thoroughfare = it))
                }
                    ?: cancellableContinuation.resume(null)
            }
                ?: cancellableContinuation.resume(null)
        }
    }

    fun add(hour: Int, minute: Int) {
        viewModelScope.launch {
            val notice = Notice(hour = hour, minute = minute)
            val id = noticeRepository.add(notice)

            if (id > Long.negativeOne) {
                alarmScheduler.schedule(notice.copy(id = id))
            }
        }
    }

    fun alarmOff() {
        viewModelScope.launch {
            noticeState.selected().map {
                it.copy(on = false)
            }
                .let {
                    it.forEach { notice ->
                        alarmScheduler.cancel(notice)
                    }

                    noticeRepository.updateAll(it)
                }
        }
    }

    fun alarmOn() {
        viewModelScope.launch {
            noticeState.selected().map {
                it.copy(on = true)
            }
                .let {
                    it.forEach { notice ->
                        alarmScheduler.schedule(notice)
                    }

                    noticeRepository.updateAll(it)
                }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            noticeRepository.deleteAll(noticeState.selected())

            noticeState.selected().forEach {
                alarmScheduler.cancel(it)
            }

            selected.value = persistentSetOf()
        }
    }

    fun load() {
        locationPermissions.any {
            checkSelfPermission(it)
        }
            .ifTrue {
                viewModelScope.launch {
                    location.value = locationProvider.getLocation()
                }
            }
    }

    fun notifyPermissionsDenied(
        permissions: Collection<String>
    ) {
        if (permissions.containsAll(locationPermissions)) {
            location.value = Complete.Failure(
                PermissionsDeniedException(locationPermissions)
            )
        }

        requestPermissionsState.update {
            val immutableList = buildSet {
                addAll(permissions)
                addAll(it.permissions)
                removeAll(locationPermissions)
            }
                .toImmutableList()

            it.copy(permissions = immutableList)
        }
    }

    fun notifyPermissionsGranted(
        permissions: Collection<String>
    ) {
        requestPermissionsState.update {
            val immutableList = it.permissions
                .toMutableList()
                .apply {
                    removeAll(permissions)
                }.toImmutableList()

            it.copy(permissions = immutableList)
        }
    }

    fun update(notice: Notice) {
        viewModelScope.launch {
            noticeRepository.update(notice)

            if (notice.on) {
                alarmScheduler.schedule(notice)
            } else {
                alarmScheduler.cancel(notice)
            }
        }
    }

    private fun StateFlow<NoticeState>.selected(): List<Notice> = with(value) {
        if (this is NoticeState.Content) {
            notices.filter {
                it.id in selected
            }
        } else {
            emptyList()
        }
    }
}
