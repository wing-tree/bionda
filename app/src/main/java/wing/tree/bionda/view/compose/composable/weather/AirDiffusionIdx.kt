package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.extension.delayHourOfDayBy
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.timeRange
import wing.tree.bionda.data.model.LivingWthrIdx
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.data.top.level.timeFormat
import wing.tree.bionda.extension.level
import wing.tree.bionda.view.compose.composable.core.Loading

@Composable
fun AirDiffusionIdx(
    state: State<LivingWthrIdx.AirDiffusionIdx>,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Crossfade(targetState = state, label = String.empty) {
            when (it) {
                State.Loading -> Loading(modifier = Modifier)
                is Complete -> when (it) {
                    is Complete.Success -> Content(value = it.value)
                    is Complete.Failure -> Text(it.exception.message ?: "${it.exception}")
                }
            }
        }
    }
}

@Composable
private fun Content(
    value: LivingWthrIdx.AirDiffusionIdx,
    modifier: Modifier = Modifier
) {
    val date = value.item.date

    Column(modifier = modifier.padding(16.dp)) {
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(value.items) { item ->
               Item(
                   item = item,
                   date = date,
                   modifier = Modifier.padding(horizontal = 8.dp)
               )
            }
        }
    }
}

@Composable
private fun Item(
    item: LivingWthrIdx.H,
    date: String,
    modifier: Modifier = Modifier
) {
    val koreaCalendar = koreaCalendar(timeFormat.parse(date))
        .delayHourOfDayBy(item.n)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = koreaCalendar.timeRange)
        Text(
            text = item.level,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}
