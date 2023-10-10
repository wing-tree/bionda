package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.isNotNanOrBlank
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.view.compose.composable.core.Loading

@Composable
fun Area(
    area: State<Area>,
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = area,
        modifier = modifier,
        label = String.empty
    ) {
        when (it) {
            State.Loading -> Loading(modifier = Modifier.fillMaxSize())
            is Complete -> when (it) {
                is Complete.Success -> Success(
                    value = it.value,
                    modifier = Modifier.fillMaxSize()
                )

                is Complete.Failure -> {}
            }
        }
    }
}

@Composable
private fun Success(
    value: Area,
    modifier: Modifier
) {
    val text = with(value) {
        when {
            level3.isNotNanOrBlank() `is` true -> level3
            level2.isNotNanOrBlank() `is` true -> level2
            level1.isNotNanOrBlank() `is` true -> level1
            else -> null
        }
    }

    if (text.isNotNull()) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text)
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null
            )
        }
    }
}
