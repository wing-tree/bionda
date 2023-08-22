package wing.tree.bionda.view.compose.composable.core

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import wing.tree.bionda.data.extension.degree
import wing.tree.bionda.data.extension.zero

@Composable
fun DegreeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current
) {
    val textMeasurer = rememberTextMeasurer()

    Text(
        text = text,
        modifier = modifier.drawWithContent {
            drawContent()
            drawText(
                textMeasurer = textMeasurer,
                text = String.degree,
                topLeft = Offset(size.width, Float.zero),
                style = style
            )
        },
        color = color,
        style = style
    )
}
