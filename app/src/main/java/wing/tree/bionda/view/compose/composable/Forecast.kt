package wing.tree.bionda.view.compose.composable

import android.icu.text.SimpleDateFormat
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
import wing.tree.bionda.data.extension.celsius
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.model.Category
import wing.tree.bionda.data.model.CodeValue
import wing.tree.bionda.data.regular.fcstCalendar
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.view.state.ForecastState
import java.util.Locale

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
        label = String.empty,
        contentKey = {
            it::class.qualifiedName
        }
    ) {
        when (it) {
            ForecastState.Loading -> Loading(modifier = Modifier)

            is ForecastState.Content -> LazyRow(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
        val fcstCalendar = fcstCalendar(item.fcstHour)
        val simpleDateFormat = SimpleDateFormat("a h시", Locale.KOREA)

        Text(text = simpleDateFormat.format(fcstCalendar))

        val pty = item.items[Category.VilageFcst.PTY]
        val sky = item.items[Category.VilageFcst.SKY]
        val tmp = item.items[Category.VilageFcst.TMP]
        val reh = item.items[Category.VilageFcst.REH]

        if (pty `is` String.zero) {
            CodeValue.sky[sky]?.let {
                Text(text = it)
            }
        } else {
            CodeValue.pty[pty]?.let {
                Text(text = it)
            }
        }

        tmp?.let {
            Text(text = "$it${String.celsius}")
        }

        reh?.let {
            Text(text = it)
        }
    }
}
