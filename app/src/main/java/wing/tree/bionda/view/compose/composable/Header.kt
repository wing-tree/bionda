package wing.tree.bionda.view.compose.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.core.Address
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.string
import wing.tree.bionda.model.UltraSrtNcst
import wing.tree.bionda.view.compose.composable.core.DegreeText
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.compose.composable.core.TextClock
import wing.tree.bionda.view.compose.composable.core.VerticalSpacer
import wing.tree.bionda.view.state.HeaderState

@Composable
fun Header(
    state: State<HeaderState>,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state,
        label = String.empty,
        contentKey = {
            it::class.qualifiedName
        }
    ) { targetState ->
        when (targetState) {
            State.Loading -> Loading(modifier = Modifier)
            is State.Complete.Success -> Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val value = targetState.value

                Column(
                    modifier = Modifier.weight(Float.one),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextClock()
                    VerticalSpacer(height = 8.dp)
                    Address(address = value.address)
                }

                UltraSrtNcst(
                    ultraSrtNcst = value.ultraSrtNcst,
                    modifier = Modifier.weight(Float.one)
                )
            }
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
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = thoroughfare)
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun UltraSrtNcst(
    ultraSrtNcst: UltraSrtNcst,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        with(ultraSrtNcst) {
            t1h?.let {
                DegreeText(
                    text = "$it",
                    style = typography.headlineLarge
                )
                
                DegreeText(text = feelsLikeTemperature?.string ?: String.empty)
            }

            pty.value?.let {
                Text(text = it)
            }

            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                DegreeText(text = tmx ?: String.empty)
                DegreeText(text = tmn ?: String.empty)
            }
        }
    }
}
