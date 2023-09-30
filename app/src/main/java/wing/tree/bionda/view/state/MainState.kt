package wing.tree.bionda.view.state

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.model.Alarm
import wing.tree.bionda.data.model.LivingWthrIdx
import wing.tree.bionda.data.model.MidLandFcstTa
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

        object Add : Action

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

data class WeatherState(
    val airDiffusionIdx: State<LivingWthrIdx.AirDiffusionIdx>,
    val midLandFcstTa: State<MidLandFcstTa>,
    val ultraSrtNcst: State<UltraSrtNcst>,
    val uvIdx: State<LivingWthrIdx.UVIdx>,
    val vilageFcst: State<VilageFcst>
) {
    sealed interface Action {
        object Refresh : Action

        sealed interface Click : Action {
            object Area : Click
        }
    }

    companion object {
        val initialValue = WeatherState(
            airDiffusionIdx = State.Loading,
            midLandFcstTa = State.Loading,
            ultraSrtNcst = State.Loading,
            uvIdx = State.Loading,
            vilageFcst = State.Loading
        )
    }
}
