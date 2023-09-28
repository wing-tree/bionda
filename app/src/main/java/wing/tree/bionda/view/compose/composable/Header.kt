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
import wing.tree.bionda.data.extension.degree
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.full
import wing.tree.bionda.data.extension.ifNull
import wing.tree.bionda.data.extension.isNotNull
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
                    modifier = Modifier.weight(Float.full),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextClock()
                    VerticalSpacer(height = 8.dp)
                    Address(address = value.address)
                }

                UltraSrtNcst(
                    ultraSrtNcst = value.ultraSrtNcst,
                    modifier = Modifier.weight(Float.full)
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = buildString {
                        t1h?.let {
                            append("$it")
                            append(String.degree)
                        }

                        append(String.empty)
                    },
                    style = typography.headlineLarge
                )

                DegreeText(text = "${feelsLikeTemperature.ifNull(String::empty)}")
            }

            Text(text = "${pty.value}")

            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                DegreeText(text = tmx ?: String.empty)
                DegreeText(text = tmn ?: String.empty)
            }
        }
    }
}
