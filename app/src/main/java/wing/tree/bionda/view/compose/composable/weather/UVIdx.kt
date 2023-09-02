package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.model.UVIdx
import wing.tree.bionda.view.compose.composable.core.Loading

@Composable
fun UVIdx(
    state: State<UVIdx>,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        AnimatedContent(
            targetState = state,
            label = String.empty,
            contentKey = {
                it::class.qualifiedName
            }
        ) {
            when (it) {
                State.Loading -> Loading(modifier = Modifier)
                is State.Complete -> when (it) {
                    is State.Complete.Success -> Content(uvIdx = it.value)
                    is State.Complete.Failure -> Text(it.throwable.message ?: "${it.throwable}")
                }
            }
        }
    }
}

@Composable
private fun Content(
    uvIdx: UVIdx,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier) {
        items(uvIdx.item) {
            Text(it)
        }
    }
}
