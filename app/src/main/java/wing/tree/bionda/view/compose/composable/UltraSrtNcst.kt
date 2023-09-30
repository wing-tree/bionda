package wing.tree.bionda.view.compose.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wing.tree.bionda.data.core.Address
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.full
import wing.tree.bionda.data.extension.ifNull
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.model.UltraSrtNcst
import wing.tree.bionda.view.compose.composable.core.DegreeText
import wing.tree.bionda.view.compose.composable.core.HorizontalSpacer
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.state.WeatherState.Action

@Composable
fun UltraSrtNcst(
    state: State<UltraSrtNcst>,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state,
        modifier = modifier,
        label = String.empty,
        contentKey = {
            it::class.qualifiedName
        }
    ) { targetState ->
        when (targetState) {
            State.Loading -> Loading(modifier = Modifier)
            is State.Complete.Success -> Content(
                ultraSrtNcst = targetState.value,
                onAction = onAction
            )
            is State.Complete.Failure -> {
                val text = targetState.exception.message ?: "${targetState.exception}"

                Text(text = text)
            }
        }
    }
}

@Composable
private fun Address(
    address: Address?,
    modifier: Modifier = Modifier
) {
    val thoroughfare = address?.thoroughfare

    if (thoroughfare.isNotNull()) {
        val imageVector = Icons.Default.LocationOn

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalSpacer(width = imageVector.defaultWidth)
            Text(text = thoroughfare)
            Icon(
                imageVector = imageVector,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun Content(
    ultraSrtNcst: UltraSrtNcst,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        with(ultraSrtNcst) {
            Column(
                modifier = Modifier.weight(Float.full),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Address(
                    address = address,
                    modifier = Modifier.clickable {
                        onAction(Action.Click.Area)
                    }
                )

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
