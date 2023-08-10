package wing.tree.bionda.model

import android.text.TextPaint
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wing.tree.bionda.extension.toTextPaint

data class Chart(
    val fcstHour: FcstHour,
    val reh: Reh,
    val segment: Segment,
    val tmp: Tmp
) {
    @JvmInline
    value class Segment(val width: Dp)

    sealed interface Element {
        val textPaint: TextPaint?
            @Composable get
    }

    object FcstHour : Element {
        override val textPaint: TextPaint
            @Composable
            get() = typography.labelSmall
                .copy(textAlign = TextAlign.Center)
                .toTextPaint()
    }

    data class Tmp(
        val chart: Chart
    ) : Element {
        data class Chart(
            val color: Color,
            val height: Dp
        )

        override val textPaint: TextPaint
            @Composable
            get() = typography.labelLarge
                .copy(textAlign = TextAlign.Center)
                .toTextPaint()
    }

    object Reh : Element {
        override val textPaint: TextPaint
            @Composable
            get() = typography.labelMedium
                .copy(textAlign = TextAlign.Center)
                .toTextPaint()
    }

    companion object {
        val default = Chart(
            fcstHour = FcstHour,
            reh = Reh,
            segment = Segment(width = 64.dp),
            tmp = Tmp(
                Tmp.Chart(
                    color = Color.Cyan,
                    height = 64.dp
                )
            )
        )
    }
}
