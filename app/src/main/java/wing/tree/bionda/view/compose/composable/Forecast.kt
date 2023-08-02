package wing.tree.bionda.view.compose.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.extension.EMPTY
import wing.tree.bionda.data.model.Category
import wing.tree.bionda.data.model.CodeValue
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.view.state.ForecastState

@Composable
fun Forecast(
    state: ForecastState,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state,
        modifier = modifier,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = String.EMPTY,
        contentKey = {
            it::class.qualifiedName
        }
    ) {
        when (it) {
            ForecastState.Loading -> Loading(modifier = Modifier)

            is ForecastState.Content -> LazyRow(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.0.dp)
            ) {
                items(it.forecast.items) { item ->
                    Item(item = item)
                }
            }

            is ForecastState.Error -> {
                Column {
                    Text(text = "${it.throwable}")
                }
            }
        }
    }
}

@Composable
private fun Item(
    item: Forecast.Item,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "${item.fcstTime}")

        item.items[Category.UltraSrtFcst.SKY]?.let {
            val text = CodeValue.sky[it] ?: return@let

            Text(text = text)
        }

        item.items[Category.UltraSrtFcst.PTY]?.let {
            val text = CodeValue.pty[it] ?: return@let

            Text(text = text)
        }

        item.items[Category.UltraSrtFcst.T1H]?.let {
            Text(text = it)
        }

        item.items[Category.UltraSrtFcst.REH]?.let {
            Text(text = it)
        }
    }
}
