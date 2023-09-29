package wing.tree.bionda.view.model

import android.app.Application
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.core.flatMap
import wing.tree.bionda.data.core.isSuccess
import wing.tree.bionda.data.core.map
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.fiveSecondsInMilliseconds
import wing.tree.bionda.data.extension.flatMap
import wing.tree.bionda.data.extension.long
import wing.tree.bionda.data.extension.negativeOne
import wing.tree.bionda.data.model.Alarm
import wing.tree.bionda.data.model.MidLandFcstTa
import wing.tree.bionda.data.provider.LocationProvider
import wing.tree.bionda.data.repository.AlarmRepository
import wing.tree.bionda.data.repository.LivingWthrIdxRepository
import wing.tree.bionda.data.repository.WeatherRepository
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.exception.PermissionsDeniedException
import wing.tree.bionda.extension.getAddress
import wing.tree.bionda.extension.insertLCRiseSetInfo
import wing.tree.bionda.extension.prependVilageFcst
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.mapper.UltraSrtNcstMapper
import wing.tree.bionda.mapper.VilageFcstMapper
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.top.level.emptyPersistentSet
import wing.tree.bionda.view.state.AlarmState
import wing.tree.bionda.view.state.MainState
import wing.tree.bionda.view.state.WeatherState
import javax.inject.Inject
import wing.tree.bionda.data.model.LCRiseSetInfo.Local as LCRiseSetInfo

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    locationProvider: LocationProvider,
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val livingWthrIdxRepository: LivingWthrIdxRepository,
    private val ultraSrtNcstMapper: UltraSrtNcstMapper,
    private val vilageFcstMapper: VilageFcstMapper,
    private val weatherRepository: WeatherRepository
) : LocationProviderViewModel(application, locationProvider) {
    private val airDiffusionIdx =  location.flatMap {
        livingWthrIdxRepository.getAirDiffusionIdx(it)
    }
        .stateIn(initialValue = State.Loading)

    private val lcRiseSetInfo: StateFlow<State<ImmutableList<LCRiseSetInfo>>> = location.flatMap {
        weatherRepository.getLCRiseSetInfo(it)
    }
        .stateIn(initialValue = State.Loading)

    private val midLandFcstTa: StateFlow<State<MidLandFcstTa>> = location.flatMap {
        weatherRepository.getMidLandFcstTa(it)
    }
        .stateIn(initialValue = State.Loading)

    private val requestPermissions = MutableStateFlow<PersistentSet<String>>(emptyPersistentSet())
    private val ultraSrtNcst = location.flatMap {
        val (nx, ny) = it.toCoordinate()
        val address = it.getAddress(getApplication())
        val baseDate = koreaCalendar.baseDate

        val (tmn, tmx) = with(weatherRepository) {
            getTmn(baseDate)?.value to getTmx(baseDate)?.value
        }

        weatherRepository.getUltraSrtNcst(nx = nx, ny = ny).map { dataModel ->
            ultraSrtNcstMapper.toPresentationModel(
                address = address,
                dataModel = dataModel,
                tmn = tmn,
                tmx = tmx
            )
        }
    }
        .stateIn(initialValue = State.Loading)

    private val ultraSrtFcst = coordinate.flatMap { (nx, ny) ->
        weatherRepository.getUltraSrtFcst(nx = nx, ny = ny).map { ultraSrtFcst ->
            vilageFcstMapper.toPresentationModel(ultraSrtFcst)
        }
    }

    private val uvIdx = location.flatMap {
        livingWthrIdxRepository.getUVIdx(it)
    }

    private val vilageFcst = combine(
        coordinate,
        lcRiseSetInfo,
        ultraSrtFcst
    ) { coordinate, lcRiseSetInfo, ultraSrtFcst ->
        coordinate.flatMap { (nx, ny) ->
            weatherRepository.getVilageFcst(nx = nx, ny = ny).map { vilageFcst ->
                vilageFcstMapper
                    .toPresentationModel(vilageFcst)
                    .run {
                        if (ultraSrtFcst.isSuccess()) {
                            overwrite(ultraSrtFcst.value)
                        } else {
                            this
                        }
                    }
            }
                .insertLCRiseSetInfo(lcRiseSetInfo)
        }
    }
        .stateIn(initialValue = State.Loading)

    val inSelectionMode = MutableStateFlow(false)
    val selected = MutableStateFlow<PersistentSet<Long>>(persistentSetOf())

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
                alarms = alarm.value,
                selected = selected
            )

            is Complete.Failure -> AlarmState.Error(
                requestPermissions = requestPermissions,
                throwable = alarm.exception
            )
        }
    }
        .stateIn(initialValue = AlarmState.initialValue)

    private val weatherState = combine(
        airDiffusionIdx,
        midLandFcstTa,
        ultraSrtNcst,
        uvIdx,
        vilageFcst
    ) { airDiffusionIdx, midLandFcstTa, ultraSrtNcst, uvIdx, vilageFcst ->
        WeatherState(
            airDiffusionIdx = airDiffusionIdx,
            midLandFcstTa = midLandFcstTa.prependVilageFcst(vilageFcst),
            ultraSrtNcst = ultraSrtNcst,
            uvIdx = uvIdx,
            vilageFcst = vilageFcst
        )
    }
        .stateIn(initialValue = WeatherState.initialValue)

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
    }
        .stateIn(initialValue = MainState.initialValue)

    private fun refresh() = load()

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
            alarmState.selected().map { alarm ->
                alarm.copy(on = false).also {
                    alarmScheduler.cancel(it)
                }
            }.let {
                alarmRepository.updateAll(it)
            }
        }
    }

    fun alarmOn() {
        viewModelScope.launch {
            alarmState.selected().map { alarm ->
                alarm.copy(on = true).also {
                    alarmScheduler.schedule(it)
                }
            }.let {
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

            selected.value = emptyPersistentSet()
        }
    }

    fun notifyPermissionsDenied(permissions: Collection<String>) {
        if (permissions.containsAll(locationPermissions)) {
            location.value = Complete.Failure(
                PermissionsDeniedException(locationPermissions)
            )
        }

        requestPermissions.update {
            it.addAll(permissions).removeAll(locationPermissions)
        }
    }

    fun notifyPermissionDenied(permission: String) {
        if (permission in locationPermissions) {
            location.value = Complete.Failure(
                PermissionsDeniedException(persistentListOf(permission))
            )
        }

        requestPermissions.update {
            it.add(permission).removeAll(locationPermissions)
        }
    }

    fun notifyPermissionGranted(permission: String) {
        requestPermissions.update {
            it.remove(permission)
        }
    }

    fun onAction(action: WeatherState.Action) {
        when (action) {
            WeatherState.Action.Refresh -> refresh()
        }
    }

    fun update(alarm: Alarm) {
        viewModelScope.launch {
            alarmRepository.update(alarm)
            alarmScheduler.scheduleOrCancel(alarm)
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

    private fun <T> Flow<T>.stateIn(initialValue: T) = stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Long.fiveSecondsInMilliseconds),
        initialValue = initialValue
    )
}
