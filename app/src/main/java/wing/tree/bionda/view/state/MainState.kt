package wing.tree.bionda.view.state

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import wing.tree.bionda.data.model.Address
import wing.tree.bionda.data.model.Alarm
import wing.tree.bionda.data.model.weather.MidLandFcstTa
import wing.tree.bionda.data.model.weather.UltraSrtNcst
import wing.tree.bionda.model.Forecast

data class MainState(
    val alarmState: AlarmState,
    val inSelectionMode: Boolean,
    val headerState: HeaderState,
    val weatherState: WeatherState
) {
    companion object {
        val initialValue = MainState(
            alarmState = AlarmState.initialValue,
            inSelectionMode = false,
            headerState = HeaderState.initialValue,
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
    val midLandFcstTaState: MidLandFcstTaState,
    val vilageFcstState: VilageFcstState
) {
    companion object {
        val initialValue = WeatherState(
            midLandFcstTaState = MidLandFcstTaState.initialValue,
            vilageFcstState = VilageFcstState.initialValue
        )
    }
}

sealed interface MidLandFcstTaState {
    object Loading : MidLandFcstTaState
    data class Content(val midLandFcstTa: MidLandFcstTa) : MidLandFcstTaState

    data class Error(val throwable: Throwable) : MidLandFcstTaState

    companion object {
        val initialValue = Loading
    }
}

sealed interface HeaderState {
    object Loading : HeaderState
    data class Content(
        val address: Address?,
        val ultraSrtNcst: UltraSrtNcst
    ) : HeaderState

    data class Error(val throwable: Throwable) : HeaderState

    companion object {
        val initialValue = Loading
    }
}

sealed interface VilageFcstState {
    object Loading : VilageFcstState
    data class Content(
        val address: Address?,
        val forecast: Forecast
    ) : VilageFcstState

    data class Error(val throwable: Throwable) : VilageFcstState

    companion object {
        val initialValue = Loading
    }
}
