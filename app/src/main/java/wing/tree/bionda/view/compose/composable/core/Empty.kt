package wing.tree.bionda.view.compose.composable.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.constant.ERROR_EMOJI

enum class Style {
    LARGE, MEDIUM
}

@Composable
fun Empty(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style.LARGE
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = ERROR_EMOJI,
                style = when(style) {
                    Style.LARGE -> typography.displayLarge
                    Style.MEDIUM -> typography.displayMedium
                }
            )

            VerticalSpacer(height = 8.dp)

            Text(
                text = text,
                style = when(style) {
                    Style.LARGE -> typography.labelLarge
                    Style.MEDIUM -> typography.labelMedium
                }
            )
        }
    }
}
