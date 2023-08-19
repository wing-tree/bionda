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
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.collections.immutable.toPersistentSet
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
import wing.tree.bionda.data.model.Alarm
import wing.tree.bionda.data.model.Result
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.provider.LocationProvider
import wing.tree.bionda.data.repository.AlarmRepository
import wing.tree.bionda.data.repository.WeatherRepository
import wing.tree.bionda.exception.PermissionsDeniedException
import wing.tree.bionda.extension.checkSelfPermission
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.model.Address
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.view.state.AlarmState
import wing.tree.bionda.view.state.MainState
import wing.tree.bionda.view.state.WeatherState
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val weatherRepository: WeatherRepository,
    private val locationProvider: LocationProvider
) : AndroidViewModel(application) {
    private val location = MutableStateFlow<Result<Location?>>(Result.Loading)
    private val requestPermissions = MutableStateFlow<ImmutableSet<String>>(persistentSetOf())
    private val stopTimeoutMillis = Long.fiveSecondsInMilliseconds
    private val weatherState = location.map {
        when (it) {
            Result.Loading -> WeatherState.Loading
            is Complete.Success -> it.data?.let { location ->
                val (nx, ny) = location.toCoordinate()
                val address = getAddress(location)
                val forecast = weatherRepository.getVilageFcst(
                    nx = nx,
                    ny = ny
                )

                when (forecast) {
                    Result.Loading -> WeatherState.Loading
                    is Complete -> when (forecast) {
                        is Complete.Success -> WeatherState.Content(
                            address = address,
                            forecast = Forecast.toPresentationModel(forecast.data)
                        )

                        is Complete.Failure -> WeatherState.Error(forecast.throwable)
                    }
                }
            } ?: WeatherState.Error(NullPointerException("Location is Null")) // TODO error define.

            is Complete.Failure -> WeatherState.Error(it.throwable)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
        initialValue = WeatherState.initialValue
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

    private val alarmState = combine(
        requestPermissions,
        alarmRepository.load(),
        selected
    ) { requestPermissions, alarm, selected ->
        when (alarm) {
            is Complete.Success -> AlarmState.Content(
                requestPermissions = requestPermissions,
                alarms = alarm.data,
                selected = selected
            )

            is Complete.Failure -> AlarmState.Error(
                requestPermissions = requestPermissions,
                throwable = alarm.throwable
            )
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
            initialValue = AlarmState.initialValue
        )

    val state: StateFlow<MainState> = combine(
        alarmState,
        inSelectionMode,
        weatherState
    ) { alarmState, inSelectionMode, weatherState ->
        MainState(
            alarmState = alarmState,
            inSelectionMode = inSelectionMode,
            weatherState = weatherState
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
            val alarm = Alarm(hour = hour, minute = minute)
            val id = alarmRepository.add(alarm)

            if (id > Long.negativeOne) {
                alarmScheduler.schedule(alarm.copy(id = id))
            }
        }
    }

    fun alarmOff() {
        viewModelScope.launch {
            alarmState.selected().map {
                it.copy(on = false)
            }
                .let {
                    it.forEach { alarm ->
                        alarmScheduler.cancel(alarm)
                    }

                    alarmRepository.updateAll(it)
                }
        }
    }

    fun alarmOn() {
        viewModelScope.launch {
            alarmState.selected().map {
                it.copy(on = true)
            }
                .let {
                    it.forEach { alarm ->
                        alarmScheduler.schedule(alarm)
                    }

                    alarmRepository.updateAll(it)
                }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            alarmRepository.deleteAll(alarmState.selected())

            alarmState.selected().forEach {
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

    fun notifyPermissionsDenied(permissions: Collection<String>) {
        if (permissions.containsAll(locationPermissions)) {
            location.value = Complete.Failure(
                PermissionsDeniedException(locationPermissions)
            )
        }

        requestPermissions.update {
            buildSet {
                addAll(permissions)
                addAll(it)
                removeAll(locationPermissions)
            }.toImmutableSet()
        }
    }

    fun notifyPermissionDenied(permission: String) {
        if (permission in locationPermissions) {
            location.value = Complete.Failure(
                PermissionsDeniedException(persistentListOf(permission))
            )
        }

        requestPermissions.update {
            buildSet {
                add(permission)
                addAll(it)
                removeAll(locationPermissions)
            }.toImmutableSet()
        }
    }

    fun notifyPermissionGranted(permission: String) {
        requestPermissions.update {
            it.toPersistentSet().remove(permission)
        }
    }

    fun update(alarm: Alarm) {
        viewModelScope.launch {
            alarmRepository.update(alarm)

            if (alarm.on) {
                alarmScheduler.schedule(alarm)
            } else {
                alarmScheduler.cancel(alarm)
            }
        }
    }

    private fun StateFlow<AlarmState>.selected(): List<Alarm> = with(value) {
        if (this is AlarmState.Content) {
            alarms.filter {
                it.id in selected
            }
        } else {
            emptyList()
        }
    }
}
