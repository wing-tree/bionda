package wing.tree.bionda.view.state

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.model.Address
import wing.tree.bionda.model.Forecast

data class MainState(
    val forecastState: ForecastState = ForecastState.initialValue,
    val inSelectionMode: Boolean = false,
    val noticeState: NoticeState = NoticeState.initialValue,
    val requestPermissionsState: RequestPermissionsState = RequestPermissionsState.initialValue
) {
    enum class Action {
        ACCESS_BACKGROUND_LOCATION,
        POST_NOTIFICATIONS
    }
}

sealed interface ForecastState {
    object Loading : ForecastState
    data class Content(
        val address: Address?,
        val forecast: Forecast
    ) : ForecastState
    data class Error(val throwable: Throwable) : ForecastState

    companion object {
        val initialValue = Loading
    }
}

sealed interface NoticeState {
    object Loading : NoticeState
    data class Content(
        val notices: ImmutableList<Notice>,
        val selected: ImmutableSet<Long> = persistentSetOf()
    ) : NoticeState

    data class Error(val throwable: Throwable) : NoticeState

    sealed interface Action {
        val notice: Notice

        data class Click(override val notice: Notice) : Action
        data class LongClick(override val notice: Notice) : Action
        data class CheckChange(override val notice: Notice, val checked: Boolean) : Action
        data class SelectedChange(override val notice: Notice, val selected: Boolean) : Action
        data class ConditionClick(override val notice: Notice, val condition: Notice.Condition) : Action
    }

    companion object {
        val initialValue = Loading
    }
}

data class RequestPermissionsState(val permissions: ImmutableList<String>) {
    companion object {
        val initialValue = RequestPermissionsState(persistentListOf())
    }
}
