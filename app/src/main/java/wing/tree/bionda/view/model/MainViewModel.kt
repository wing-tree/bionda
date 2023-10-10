package wing.tree.bionda.view.model

import android.app.Application
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.core.flatMap
import wing.tree.bionda.data.core.isSuccess
import wing.tree.bionda.data.core.map
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.flatMap
import wing.tree.bionda.data.extension.long
import wing.tree.bionda.data.extension.negativeOne
import wing.tree.bionda.data.model.Alarm
import wing.tree.bionda.data.model.MidLandFcstTa
import wing.tree.bionda.data.repository.AlarmRepository
import wing.tree.bionda.data.repository.LivingWthrIdxRepository
import wing.tree.bionda.data.repository.WeatherRepository
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.exception.PermissionsDeniedException
import wing.tree.bionda.extension.insertLCRiseSetInfo
import wing.tree.bionda.extension.prependVilageFcst
import wing.tree.bionda.mapper.UltraSrtNcstMapper
import wing.tree.bionda.mapper.VilageFcstMapper
import wing.tree.bionda.model.LivingWthrIdx
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.top.level.emptyPersistentSet
import wing.tree.bionda.top.level.noOperations
import wing.tree.bionda.view.state.AlarmState
import wing.tree.bionda.view.state.MainState
import wing.tree.bionda.view.state.WeatherState
import javax.inject.Inject
import wing.tree.bionda.data.model.LCRiseSetInfo.Local as LCRiseSetInfo

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val livingWthrIdxRepository: LivingWthrIdxRepository,
    private val weatherRepository: WeatherRepository
) : LocationProviderViewModel(application) {
    private val airDiffusionIdx =  location
        .flatMap(livingWthrIdxRepository::getAirDiffusionIdx)
        .stateIn(initialValue = State.Loading)

    private val lcRiseSetInfo: StateFlow<State<ImmutableList<LCRiseSetInfo>>> = location
        .flatMap(weatherRepository::getLCRiseSetInfo)
        .stateIn(initialValue = State.Loading)

    private val midLandFcstTa: StateFlow<State<MidLandFcstTa>> = location
        .flatMap(weatherRepository::getMidLandFcstTa)
        .stateIn(initialValue = State.Loading)

    private val requestPermissions = MutableStateFlow<PersistentSet<String>>(emptyPersistentSet())
    private val ultraSrtNcst = coordinate.flatMap {
        val baseDate = koreaCalendar.baseDate
        val (tmn, tmx) = with(weatherRepository) {
            getTmn(baseDate)?.value to getTmx(baseDate)?.value
        }

        weatherRepository.getUltraSrtNcst(nx = it.nx, ny = it.ny).map { dataModel ->
            UltraSrtNcstMapper.toPresentationModel(
                dataModel = dataModel,
                tmn = tmn,
                tmx = tmx
            )
        }
    }
        .stateIn(initialValue = State.Loading)

    private val ultraSrtFcst = coordinate.flatMap { (nx, ny) ->
        weatherRepository.getUltraSrtFcst(nx = nx, ny = ny).map { ultraSrtFcst ->
            VilageFcstMapper.toPresentationModel(ultraSrtFcst)
        }
    }

    private val uvIdx = location.flatMap(livingWthrIdxRepository::getUVIdx)
    private val livingWthrIdx = combine(airDiffusionIdx, uvIdx) { airDiffusionIdx, uvIdx ->
        LivingWthrIdx(
            airDiffusionIdx = airDiffusionIdx,
            uvIdx = uvIdx
        )
    }

    private val vilageFcst = combine(
        coordinate,
        lcRiseSetInfo,
        ultraSrtFcst
    ) { coordinate, lcRiseSetInfo, ultraSrtFcst ->
        coordinate.flatMap { (nx, ny) ->
            weatherRepository.getVilageFcst(nx = nx, ny = ny).map { vilageFcst ->
                VilageFcstMapper
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
        area,
        livingWthrIdx,
        midLandFcstTa,
        ultraSrtNcst,
        vilageFcst
    ) { area, livingWthrIdx, midLandFcstTa, ultraSrtNcst, vilageFcst ->
        WeatherState(
            area = area,
            livingWthrIdx = livingWthrIdx,
            midLandFcstTa = midLandFcstTa.prependVilageFcst(vilageFcst),
            ultraSrtNcst = ultraSrtNcst,
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
            updateLocation(Complete.Failure(PermissionsDeniedException(locationPermissions)))
        }

        requestPermissions.update {
            it.addAll(permissions).removeAll(locationPermissions)
        }
    }

    fun notifyPermissionDenied(permission: String) {
        if (permission in locationPermissions) {
            updateLocation(Complete.Failure(PermissionsDeniedException(permission)))
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
            is WeatherState.Action.Click -> noOperations
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
}
