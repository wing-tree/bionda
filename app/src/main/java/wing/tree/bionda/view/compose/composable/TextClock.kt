package wing.tree.bionda.view.compose.composable

import android.widget.TextClock
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import wing.tree.bionda.extension.toTypeface

@Composable
fun TextClock(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.titleLarge,
) {
    val textColor = color.takeOrElse {
        style.color.takeOrElse {
            LocalContentColor.current
        }
    }

    val typeface by style.toTypeface()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextClock(context).apply {
                setTextColor(textColor.toArgb())

                this.textSize = style.fontSize.value
                this.typeface = typeface
            }
        }
    )
}
