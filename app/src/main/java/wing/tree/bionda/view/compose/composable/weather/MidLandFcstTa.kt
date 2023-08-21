package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.state.MidLandFcstTaState

@Composable
fun MidLandFcstTa(
    state: MidLandFcstTaState,
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
        when(it) {
            MidLandFcstTaState.Loading -> Loading(modifier = Modifier)
            is MidLandFcstTaState.Content -> Content(
                content = it,
                modifier = Modifier.fillMaxSize()
            )
            is MidLandFcstTaState.Error -> Text("${it.throwable}")
        }
    }
}

@Composable
private fun Content(
    content: MidLandFcstTaState.Content,
    modifier: Modifier = Modifier
) {

}
