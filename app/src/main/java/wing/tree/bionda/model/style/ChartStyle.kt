package wing.tree.bionda.model.style

import android.text.TextPaint
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import wing.tree.bionda.extension.height
import wing.tree.bionda.extension.toTextPaint
import wing.tree.bionda.extension.zero
import wing.tree.bionda.theme.YellowOrange

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
    val height: Dp
        @Composable
        get() = with(LocalDensity.current) {
            fcstHour.textPaint.height.toDp()
                .plus(reh.textPaint.height.toDp())
                .plus(pcp.textPaint.height.toDp())
                .plus(pop.textPaint.height.toDp())
                .plus(tmp.textPaint.height.toDp())
                .plus(tmpChart.height)
                .plus(weatherIcon.height)
        }

    @JvmInline
    value class Segment(val width: Dp)

    data class VerticalPaddingValues(
        val top: Dp = Dp.zero,
        val bottom: Dp = Dp.zero
    )

    sealed class Element {
        open val textPaint: TextPaint?
            @Composable get() = null

        open val verticalPaddingValues: VerticalPaddingValues =
            VerticalPaddingValues()
    }

    object FcstHour : Element() {
        override val textPaint: TextPaint
            @Composable
            get() = typography.labelSmall
                .copy(textAlign = TextAlign.Center)
                .toTextPaint()
    }

    object Pcp : Element() {
        override val textPaint: TextPaint
            @Composable
            get() = typography.labelMedium
                .copy(textAlign = TextAlign.Center)
                .toTextPaint()
    }

    object Pop : Element() {
        override val textPaint: TextPaint
            @Composable
            get() = typography.labelMedium
                .copy(textAlign = TextAlign.Center)
                .toTextPaint()
    }

    object Reh : Element() {
        override val textPaint: TextPaint
            @Composable
            get() = typography.labelMedium
                .copy(textAlign = TextAlign.Center)
                .toTextPaint()
    }

    object Tmp : Element() {
        override val textPaint: TextPaint
            @Composable
            get() = typography.labelMedium
                .copy(textAlign = TextAlign.Center)
                .toTextPaint()
    }

    data class TmpChart(
        val color: Color,
        val height: Dp
    ) : Element()

    data class WeatherIcon(
        val size: DpSize,
        val color: Color
    ) : Element() {
        val width: Dp = size.width
        val height: Dp = size.height
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
                color = YellowOrange,
                height = 16.dp
            ),
            weatherIcon = WeatherIcon(
                size = DpSize(width = 30.dp, height = 30.dp),
                color = Color.Unspecified
            )
        )
    }
}
