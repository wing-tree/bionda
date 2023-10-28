package wing.tree.bionda.view.state

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentSetOf
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.model.Alarm
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.data.model.MidLandFcstTa
import wing.tree.bionda.model.LivingWthrIdx
import wing.tree.bionda.model.UltraSrtNcst
import wing.tree.bionda.model.VilageFcst

data class MainState(
    val alarmState: AlarmState,
    val inSelectionMode: Boolean,
    val weatherState: WeatherState
) {
    companion object {
        val initialValue = MainState(
            alarmState = AlarmState.initialValue,
            inSelectionMode = false,
            weatherState = WeatherState.initialValue
        )
    }
}

sealed interface AlarmState {
    val requestPermissions: ImmutableSet<String>

    data class Loading(
        override val requestPermissions: ImmutableSet<String> = persistentSetOf()
    ) : AlarmState

    data class Content(
        override val requestPermissions: ImmutableSet<String>,
        val alarms: ImmutableList<Alarm>,
        val selected: ImmutableSet<Long> = persistentSetOf()
    ) : AlarmState

    data class Error(
        override val requestPermissions: ImmutableSet<String>,
        val throwable: Throwable
    ) : AlarmState

    sealed interface Action {
        enum class RequestPermissions : Action {
            ACCESS_BACKGROUND_LOCATION,
            POST_NOTIFICATIONS,
            SCHEDULE_EXACT_ALARM
        }

        enum class SelectionMode : Action {
            ALARM_OFF,
            ALARM_ON,
            DELETE_ALL
        }

        data object Add : Action

        sealed interface Alarms : Action {
            val alarm: Alarm

            data class Click(override val alarm: Alarm) : Alarms
            data class LongClick(override val alarm: Alarm) : Alarms
            data class CheckChange(override val alarm: Alarm, val checked: Boolean) : Alarms
            data class SelectedChange(override val alarm: Alarm, val selected: Boolean) : Alarms
            data class ConditionClick(override val alarm: Alarm, val condition: Alarm.Condition) : Alarms
        }
    }

    companion object {
        val initialValue = Loading()
    }
}

data class DrawerContentState(val favorites: State<PersistentList<Area>>) {
    companion object {
        val initialValue = DrawerContentState(
            favorites = State.Loading
        )
    }
}

data class WeatherState(
    val area: State<Area>,
    val livingWthrIdx: LivingWthrIdx,
    val midLandFcstTa: State<MidLandFcstTa>,
    val ultraSrtNcst: State<UltraSrtNcst>,
    val vilageFcst: State<VilageFcst>
) {
    sealed interface Action {
        data object Refresh : Action

        sealed interface Area : Action {
            data class Favorite(val areaNo: String) : Area
            data class Select(val area: wing.tree.bionda.data.model.Area) : Area
            data object Click : Area
            data object MyLocation : Area
        }
    }

    companion object {
        val initialValue = WeatherState(
            area = State.Loading,
            livingWthrIdx = LivingWthrIdx.initialValue,
            midLandFcstTa = State.Loading,
            ultraSrtNcst = State.Loading,
            vilageFcst = State.Loading
        )
    }
}
