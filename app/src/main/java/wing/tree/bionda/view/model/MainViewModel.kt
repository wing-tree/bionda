package wing.tree.bionda.view.model

import android.app.Application
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import wing.tree.bionda.data.extension.fiveSecondsInMilliseconds
import wing.tree.bionda.data.extension.ifTrue
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.model.Result
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.repository.ForecastRepository
import wing.tree.bionda.data.provider.LocationProvider
import wing.tree.bionda.data.repository.NoticeRepository
import wing.tree.bionda.exception.PermissionsDeniedException
import wing.tree.bionda.extension.checkSelfPermission
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.permissions.locationPermissions
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
                val forecast = forecastRepository.getVilageFcst(
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
            } ?: ForecastState.Error(NullPointerException())

            is Complete.Failure -> ForecastState.Error(it.throwable)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
        initialValue = ForecastState.initialValue
    )

    private val noticeState = noticeRepository
        .load()
        .map {
            when (it) {
                is Complete.Success -> NoticeState.Content(it.data)
                is Complete.Failure -> NoticeState.Error(it.throwable)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
            initialValue = NoticeState.initialValue
        )

    private val requestPermissionsState = MutableStateFlow(RequestPermissionsState.initialValue)

    val state: StateFlow<MainState> = combine(
        forecastState,
        noticeState,
        requestPermissionsState
    ) { forecastState, noticeState, requestPermissionsState ->
        MainState(
            forecastState = forecastState,
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
        val maxResults = Int.one

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.latitude, location.longitude, maxResults) {
                it.firstOrNull()?.let { address ->
                    cancellableContinuation.resume(address)
                } ?: cancellableContinuation.resume(null)
            }
        } else {
            @Suppress("DEPRECATION")
            geocoder.getFromLocation(location.latitude, location.longitude, maxResults)?.let {
                it.firstOrNull()?.let { address ->
                    cancellableContinuation.resume(address)
                } ?: cancellableContinuation.resume(null)
            } ?: cancellableContinuation.resume(null)
        }
    }

    fun add(hour: Int, minute: Int) {
        viewModelScope.launch {
            val notice = Notice(hour = hour, minute = minute)
            val notificationId = noticeRepository.add(notice)

            alarmScheduler.schedule(notice.copy(notificationId = notificationId))
        }
    }

    fun delete(notice: Notice) {
        viewModelScope.launch {
            noticeRepository.delete(notice)
            alarmScheduler.cancel(notice)
        }
    }

    fun load() {
        locationPermissions.any {
            checkSelfPermission(it)
        }.ifTrue {
            viewModelScope.launch {
                location.value = locationProvider.getLocation()
            }
        }
    }

    fun notifyPermissionsDenied(
        permissions: Collection<String>
    ) {
        with(permissions) {
            if (containsAll(locationPermissions)) {
                location.value = Complete.Failure(
                    PermissionsDeniedException(locationPermissions)
                )
            }

            requestPermissionsState.value = RequestPermissionsState(
                filterNot {
                    locationPermissions.contains(it)
                }
                    .toImmutableList()
            )
        }
    }

    fun update(notice: Notice) {
        viewModelScope.launch {
            noticeRepository.update(notice)

            if (notice.checked) {
                alarmScheduler.schedule(notice)
            } else {
                alarmScheduler.cancel(notice)
            }
        }
    }
}
