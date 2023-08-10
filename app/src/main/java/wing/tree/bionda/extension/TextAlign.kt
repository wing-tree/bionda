package wing.tree.bionda.extension

import android.graphics.Paint.Align
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import wing.tree.bionda.data.extension.`is`

fun TextAlign.toAlign(layoutDirection: LayoutDirection): Align {
    return when {
        `is`(TextAlign.Left) -> Align.LEFT
        `is`(TextAlign.Right) -> Align.RIGHT
        `is`(TextAlign.Center) -> Align.CENTER
        else -> if (layoutDirection `is` LayoutDirection.Ltr) {
            when (this) {
                TextAlign.Justify, TextAlign.Start -> Align.LEFT
                else -> Align.RIGHT
            }
        } else {
            when (this) {
                TextAlign.Justify, TextAlign.Start -> Align.RIGHT
                else -> Align.LEFT
            }
        }
    }
}
