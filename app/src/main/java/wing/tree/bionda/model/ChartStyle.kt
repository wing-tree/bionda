package wing.tree.bionda.model

import android.text.TextPaint
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wing.tree.bionda.extension.toTextPaint

data class ChartStyle(
    val segment: Segment,
    val fcstHour: FcstHour,
    val weatherIcon: WeatherIcon,
    val tmp: Tmp,
    val reh: Reh
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

    data class WeatherIcon(
        val width: Dp,
        val height: Dp,
        val color: Color
    ) : Element {
        override val textPaint: TextPaint?
            @Composable
            get() = null
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
        val default = ChartStyle(
            segment = Segment(width = 64.dp),
            fcstHour = FcstHour,
            weatherIcon = WeatherIcon(
                width = 30.dp,
                height = 30.dp,
                color = Color.Unspecified
            ),
            tmp = Tmp(
                Tmp.Chart(
                    color = Color.Cyan,
                    height = 64.dp
                )
            ),
            reh = Reh
        )
    }
}
