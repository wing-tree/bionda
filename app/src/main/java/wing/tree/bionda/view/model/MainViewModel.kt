package wing.tree.bionda.view.model

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import wing.tree.bionda.LocationProvider
import wing.tree.bionda.data.extension.FIVE_SECONDS_IN_MILLISECONDS
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.baseTime
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.model.Result
import wing.tree.bionda.data.regular.baseCalendar
import wing.tree.bionda.data.repository.ForecastRepository
import wing.tree.bionda.data.repository.NoticeRepository
import wing.tree.bionda.extension.checkSelfPermission
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.model.Coordinate
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.view.state.ForecastState
import wing.tree.bionda.view.state.MainState
import wing.tree.bionda.view.state.NoticeState
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
            is Result.Complete.Success -> {
                val (nx, ny) = it.data?.toCoordinate() ?: Coordinate.seoul

                val baseCalendar = baseCalendar()
                val forecast = forecastRepository.getUltraSrtFcst(
                    baseDate = baseCalendar.baseDate,
                    baseTime = baseCalendar.baseTime,
                    nx = nx,
                    ny = ny
                )

                when (forecast) {
                    Result.Loading -> ForecastState.Loading
                    is Result.Complete -> when (forecast) {
                        is Result.Complete.Success -> ForecastState.Content(
                            Forecast.toPresentationModel(forecast.data)
                        )

                        is Result.Complete.Failure -> ForecastState.Error(forecast.throwable)
                    }
                }
            }

            is Result.Complete.Failure -> ForecastState.Error(it.throwable)
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
                is Result.Complete -> when (it) {
                    is Result.Complete.Success -> NoticeState.Content(it.data)
                    is Result.Complete.Failure -> NoticeState.Error(it.throwable)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
            initialValue = NoticeState.initialValue
        )

    val state: StateFlow<MainState> = combine(forecastState, noticeState) { forecastState, noticeState ->
        MainState(
            forecastState = forecastState,
            noticeState = noticeState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
        initialValue = MainState()
    )

    fun add(hour: Int, minute: Int) {
        viewModelScope.launch {
            val notice = Notice(hour = hour, minute = minute)

            noticeRepository.add(notice)
            alarmScheduler.schedule(notice)
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
        }
    }

    fun update(notice: Notice) {
        viewModelScope.launch {
            noticeRepository.update(notice)
        }
    }
}
