package wing.tree.bionda.view.compose.composable

import androidx.compose.animation.AnimatedContent
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
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.model.UltraSrtNcst
import wing.tree.bionda.view.compose.composable.core.DegreeText
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.compose.composable.core.TextClock
import wing.tree.bionda.view.compose.composable.core.VerticalSpacer
import wing.tree.bionda.view.state.HeaderState

@Composable
fun Header(
    state: HeaderState,
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
            HeaderState.Loading -> Loading(modifier = Modifier)
            is HeaderState.Content -> Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (address, ultraSrtNcst) = targetState

                Column(
                    modifier = Modifier.weight(Float.one),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextClock()
                    VerticalSpacer(height = 8.dp)
                    Address(address = address)
                }

                UltraSrtNcst(
                    ultraSrtNcst = ultraSrtNcst,
                    modifier = Modifier.weight(Float.one)
                )
            }
            is HeaderState.Error -> {
                val text = targetState.throwable.message ?: "${targetState.throwable}"

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
        ultraSrtNcst.t1h?.let {

        }
        with(ultraSrtNcst) {
            t1h?.let {
                DegreeText(
                    text = "$it",
                    style = typography.headlineLarge
                )
            }

            pty.value?.let {
                Text(text = it)
            }
        }
    }
}
