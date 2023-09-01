package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.core.State.Loading
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.data.model.LCRiseSetInfo.Local as LCRiseSetInfo

@Composable
fun LCRiseSetInfo(
    state: State<ImmutableList<LCRiseSetInfo>>,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        AnimatedContent(
            targetState = state,
            modifier = Modifier,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = String.empty,
            contentKey = {
                it::class.qualifiedName
            }
        ) {
            when (it) {
                Loading -> Loading(modifier = Modifier)
                is Complete -> when (it) {
                    is Complete.Success -> Content(lcRiseSetInfo = it.value)
                    is Complete.Failure -> Text(text = it.throwable.message ?: "${it.throwable}")
                }
            }
        }
    }
}

@Composable
private fun Content(
    lcRiseSetInfo: ImmutableList<LCRiseSetInfo>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        lcRiseSetInfo.forEach {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                with(it) {
                    Text(text = sunrise)
                    Text(text = sunset)
                }
            }
        }
    }
}
