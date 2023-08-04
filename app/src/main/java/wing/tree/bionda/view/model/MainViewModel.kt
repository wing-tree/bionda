package wing.tree.bionda.view.model

import android.app.Application
import android.location.Location
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
import wing.tree.bionda.data.extension.fiveSecondsInMilliseconds
import wing.tree.bionda.data.extension.ifTrue
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.model.Result
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.repository.ForecastRepository
import wing.tree.bionda.data.provider.LocationProvider
import wing.tree.bionda.data.repository.NoticeRepository
import wing.tree.bionda.exception.PermissionsDeniedException
import wing.tree.bionda.extension.checkSelfPermission
import wing.tree.bionda.model.Coordinate
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.view.state.ForecastState
import wing.tree.bionda.view.state.MainState
import wing.tree.bionda.view.state.NoticeState
import wing.tree.bionda.view.state.RequestPermissionsState
import javax.inject.Inject

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
            is Complete.Success -> {
                val (nx, ny) = it.data?.toCoordinate() ?: Coordinate.seoul
                val forecast = forecastRepository.getVilageFcst(
                    nx = nx,
                    ny = ny
                )

                when (forecast) {
                    Result.Loading -> ForecastState.Loading
                    is Complete -> when (forecast) {
                        is Complete.Success -> ForecastState.Content(
                            Forecast.toPresentationModel(forecast.data)
                        )

                        is Complete.Failure -> ForecastState.Error(forecast.throwable)
                    }
                }
            }

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
