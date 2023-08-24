package wing.tree.bionda.model.style

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
    val reh: Reh,
    val pcp: Pcp,
    val pop: Pop,
    val tmp: Tmp,
    val tmpChart: TmpChart,
    val weatherIcon: WeatherIcon
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

    object Pcp : Element {
        override val textPaint: TextPaint
            @Composable
            get() = typography.labelMedium
                .copy(textAlign = TextAlign.Center)
                .toTextPaint()
    }

    object Pop : Element {
        override val textPaint: TextPaint
            @Composable
            get() = typography.labelMedium
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

    object Tmp : Element {
        override val textPaint: TextPaint
            @Composable
            get() = typography.labelMedium
                .copy(textAlign = TextAlign.Center)
                .toTextPaint()
    }

    data class TmpChart(
        val color: Color,
        val height: Dp
    ) : Element {
        override val textPaint: TextPaint?
            @Composable
            get() = null
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

    companion object {
        val defaultValue = ChartStyle(
            segment = Segment(width = 64.dp),
            fcstHour = FcstHour,
            pcp = Pcp,
            pop = Pop,
            reh = Reh,
            tmp = Tmp,
            tmpChart = TmpChart(
                color = Color.Cyan,
                height = 16.dp
            ),
            weatherIcon = WeatherIcon(
                width = 30.dp,
                height = 30.dp,
                color = Color.Unspecified
            )
        )
    }
}
