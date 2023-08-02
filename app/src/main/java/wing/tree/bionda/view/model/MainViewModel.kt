package wing.tree.bionda.view.model

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
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
import wing.tree.bionda.data.extension.FIVE_SECONDS_IN_MILLISECONDS
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.model.Result
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.repository.ForecastRepository
import wing.tree.bionda.data.repository.NoticeRepository
import wing.tree.bionda.extension.checkSelfPermission
import wing.tree.bionda.model.Coordinate
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.provider.LocationProvider
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.extension.toCoordinate
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
    private val stopTimeoutMillis = Long.FIVE_SECONDS_IN_MILLISECONDS
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
                Result.Loading -> NoticeState.Loading
                is Complete -> when (it) {
                    is Complete.Success -> NoticeState.Content(it.data)
                    is Complete.Failure -> NoticeState.Error(it.throwable)
                }
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
        if (checkSelfPermission(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)) {
            viewModelScope.launch {
                location.value = locationProvider.getLocation()
            }
        } else {
            location.value = Complete.Success(LocationProvider.DEFAULT_LOCATION)
        }
    }

    fun notifyMultiplePermissionsDenied(
        permissions: Collection<String>
    ) {
        requestPermissionsState.value = RequestPermissionsState(
            permissions.toImmutableList()
        )
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
