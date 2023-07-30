package wing.tree.bionda.view.state

import kotlinx.collections.immutable.ImmutableList
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.model.Forecast

data class MainState(
    val forecastState: ForecastState = ForecastState.initialValue,
    val noticeState: NoticeState = NoticeState.initialValue
)

sealed interface ForecastState {
    object Loading : ForecastState
    data class Content(val forecast: Forecast) : ForecastState
    data class Error(val throwable: Throwable) : ForecastState

    companion object {
        val initialValue = Loading
    }
}

sealed interface NoticeState {
    object Loading : NoticeState
    data class Content(val notices: ImmutableList<Notice>) : NoticeState
    data class Error(val throwable: Throwable) : NoticeState

    companion object {
        val initialValue = Loading
    }
}
