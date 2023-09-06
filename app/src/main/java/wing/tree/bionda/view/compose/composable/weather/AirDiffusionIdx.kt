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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.model.LivingWthrIdx
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
                is State.Complete -> when (it) {
                    is State.Complete.Success -> Content(value = it.value)
                    is State.Complete.Failure -> Text(it.exception.message ?: "${it.exception}")
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
    Column(modifier = modifier.padding(16.dp)) {
        val date = value.item.date

        Text(text = date) // TODO Remove,
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(value.items) { item ->
               Text(text = item.h, modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
    }
}
