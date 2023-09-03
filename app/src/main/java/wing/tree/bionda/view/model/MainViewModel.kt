package wing.tree.bionda.view.model

import android.app.Application
import android.location.Location
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.lifecycle.AndroidViewModel
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wing.tree.bionda.data.core.Address
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.core.flatMap
import wing.tree.bionda.data.core.isSuccess
import wing.tree.bionda.data.core.map
import wing.tree.bionda.data.extension.fiveSecondsInMilliseconds
import wing.tree.bionda.data.extension.ifTrue
import wing.tree.bionda.data.extension.long
import wing.tree.bionda.data.extension.negativeOne
import wing.tree.bionda.data.model.Alarm
import wing.tree.bionda.data.model.MidLandFcstTa
import wing.tree.bionda.data.model.UltraSrtNcst
import wing.tree.bionda.data.provider.LocationProvider
import wing.tree.bionda.data.repository.AlarmRepository
import wing.tree.bionda.data.repository.WeatherRepository
import wing.tree.bionda.exception.PermissionsDeniedException
import wing.tree.bionda.extension.checkSelfPermission
import wing.tree.bionda.extension.getAddress
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.mapper.UltraSrtNcstMapper
import wing.tree.bionda.mapper.VilageFcstMapper
import wing.tree.bionda.model.VilageFcst
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.top.level.emptyPersistentSet
import wing.tree.bionda.view.state.AlarmState
import wing.tree.bionda.view.state.HeaderState
import wing.tree.bionda.view.state.MainState
import wing.tree.bionda.view.state.WeatherState
import javax.inject.Inject
import wing.tree.bionda.data.model.LCRiseSetInfo.Local as LCRiseSetInfo

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val locationProvider: LocationProvider,
    private val ultraSrtNcstMapper: UltraSrtNcstMapper,
    private val vilageFcstMapper: VilageFcstMapper,
    private val weatherRepository: WeatherRepository
) : AndroidViewModel(application) {
    private val location = MutableStateFlow<State<Location>>(State.Loading)
    private val coordinate = location.map {
        it.map(Location::toCoordinate)
    }

    private val requestPermissions = MutableStateFlow<PersistentSet<String>>(emptyPersistentSet())
    private val headerState = location.map {
        when (it) {
            State.Loading -> HeaderState.Loading
            is Complete.Success -> it.value.let { location ->
                val (nx, ny) = location.toCoordinate()
                val address = location.getAddress(getApplication())
                val ultraSrtNcst = weatherRepository.getUltraSrtNcst(nx = nx, ny = ny)

                ultraSrtNcst.asState(address)
            }

            is Complete.Failure -> HeaderState.Error(it.exception)
        }
    }
        .stateIn(initialValue = HeaderState.initialValue)

    private val lcRiseSetInfo: StateFlow<State<ImmutableList<LCRiseSetInfo>>> = location.map {
        it.flatMap { location ->
            weatherRepository.getLCRiseSetInfo(location)
        }
    }
        .stateIn(initialValue = State.Loading)

    private val midLandFcstTa: StateFlow<State<MidLandFcstTa>> = location.map {
        it.flatMap { location ->
            weatherRepository.getMidLandFcstTa(location)
        }
    }
        .stateIn(initialValue = State.Loading)

    private val uvIdx = location.map {
        it.flatMap { location ->
            weatherRepository.getUVIdx(location)
        }
    }

    private val ultraSrtFcst = coordinate.map {
        it.flatMap { (nx, ny) ->
            weatherRepository.getUltraSrtFcst(nx = nx, ny = ny).map { ultraSrtFcst ->
                vilageFcstMapper.toPresentationModel(
                    ultraSrtFcst,
                    VilageFcst.Item.Type.UltraSrtFcst
                )
            }
        }
    }

    private val vilageFcst = combine(
        coordinate,
        ultraSrtFcst
    ) { coordinate, ultraSrtFcst ->
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
        lcRiseSetInfo,
        midLandFcstTa,
        uvIdx,
        vilageFcst
    ) { lcRiseSetInfo, midLandFcstTa, uvIdx, vilageFcst ->
        WeatherState(
            lcRiseSetInfo = lcRiseSetInfo,
            midLandFcstTa = midLandFcstTa,
            uvIdx = uvIdx,
            vilageFcst = vilageFcst.insertLCRiseSetInfo(lcRiseSetInfo)
        )
    }
        .stateIn(initialValue = WeatherState.initialValue)

    val state: StateFlow<MainState> = combine(
        alarmState,
        inSelectionMode,
        headerState,
        weatherState
    ) { alarmState, inSelectionMode, ultraSrtNcstState, weatherState ->
        MainState(
            alarmState = alarmState,
            inSelectionMode = inSelectionMode,
            headerState = ultraSrtNcstState,
            weatherState = weatherState
        )
    }
        .stateIn(initialValue = MainState.initialValue)

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

    fun load() {
        locationPermissions.any {
            checkSelfPermission(it)
        }.ifTrue {
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

    fun update(alarm: Alarm) {
        viewModelScope.launch {
            alarmRepository.update(alarm)
            alarmScheduler.scheduleOrCancel(alarm)
        }
    }

    private fun Complete<UltraSrtNcst.Local>.asState(address: Address?): HeaderState = when (this) {
        is Complete.Success -> HeaderState.Content(
            address = address,
            ultraSrtNcst = ultraSrtNcstMapper.toPresentationModel(value)
        )

        is Complete.Failure -> HeaderState.Error(exception)
    }

    private fun State<VilageFcst>.insertLCRiseSetInfo(
        lcRiseSetInfo: State<ImmutableList<LCRiseSetInfo>>
    ) = map {
        with(lcRiseSetInfo) {
            if (isSuccess()) {
                it.insertLCRiseSetInfo(value)
            } else {
                it
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

    private fun <T> Flow<T>.stateIn(
        initialValue: T
    ) = stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Long.fiveSecondsInMilliseconds),
        initialValue = initialValue
    )
}
