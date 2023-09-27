package wing.tree.bionda.extension

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.extension.float
import wing.tree.bionda.data.extension.full
import wing.tree.bionda.data.extension.isNotZero
import wing.tree.bionda.data.extension.onePercent
import kotlin.math.min

private fun ContentDrawScope.drawBottomFadingEdge(
    scrollState: ScrollState,
    length: Dp
) = with(scrollState) {
    val endY = size.height.minus(maxValue).plus(value)
    val fadingEdgeLength = min(length.toPx(), maxValue.minus(value).float)
    val startY = endY.minus(fadingEdgeLength)

    if (fadingEdgeLength.isNotZero()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Black, Color.Transparent),
                startY = startY,
                endY = endY
            ),
            blendMode = BlendMode.DstIn
        )
    }
}

private fun ContentDrawScope.drawTopFadingEdge(
    scrollState: ScrollState,
    length: Dp
) {
    val startY = scrollState.value.float
    val endY = startY.plus(min(length.toPx(), startY))

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color.Black),
            startY = startY,
            endY = endY
        ),
        blendMode = BlendMode.DstIn
    )
}

fun Modifier.verticalFadingEdge(
    scrollState: ScrollState,
    length: Dp = 16.dp
): Modifier = then(
    Modifier
        .graphicsLayer {
            alpha = with(Float) {
                full.minus(onePercent)
            }
        }.drawWithContent {
            drawContent()

            drawTopFadingEdge(
                scrollState = scrollState,
                length = length
            )

            drawBottomFadingEdge(
                scrollState = scrollState,
                length = length
            )
        }
)
