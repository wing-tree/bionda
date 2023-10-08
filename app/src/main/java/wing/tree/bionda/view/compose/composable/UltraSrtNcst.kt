package wing.tree.bionda.view.compose.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.full
import wing.tree.bionda.data.extension.ifNull
import wing.tree.bionda.model.UltraSrtNcst
import wing.tree.bionda.view.compose.composable.core.DegreeText
import wing.tree.bionda.view.compose.composable.core.Loading

@Composable
fun UltraSrtNcst(
    state: State<UltraSrtNcst>,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state,
        modifier = modifier,
        label = String.empty,
        contentKey = {
            it::class.qualifiedName
        }
    ) {
        when (it) {
            State.Loading -> Loading(modifier = Modifier)
            is State.Complete.Success -> Content(ultraSrtNcst = it.value)
            is State.Complete.Failure -> {
                val text = it.exception.message ?: "${it.exception}"

                Text(text = text)
            }
        }
    }
}

@Composable
private fun Content(
    ultraSrtNcst: UltraSrtNcst,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        with(ultraSrtNcst) {
            Column(
                modifier = Modifier.weight(Float.full),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DegreeText(
                    text = "${t1h.ifNull(String::empty)}",
                    style = typography.displayMedium
                )

                DegreeText(text = "${feelsLikeTemperature.ifNull(String::empty)}")
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    DegreeText(text = tmx ?: String.empty)
                    DegreeText(text = tmn ?: String.empty)
                }
            }

            Column(
                modifier = Modifier.weight(Float.full),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = pty.value ?: String.empty)
            }
        }
    }
}
