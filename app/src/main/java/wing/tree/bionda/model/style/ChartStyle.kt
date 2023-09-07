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
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import wing.tree.bionda.data.extension.quarter
import wing.tree.bionda.extension.height
import wing.tree.bionda.extension.toTextPaint
import wing.tree.bionda.extension.zero
import wing.tree.bionda.theme.YellowOrange

data class ChartStyle(
    val segment: Segment,
    val apparentTemperature: Text,
    val fcstTime: Text,
    val reh: Text,
    val pcp: Text,
    val pop: Text,
    val tmp: Tmp,
    val tmpChart: TmpChart,
    val weatherIcon: WeatherIcon,
    val wsd: Text
) {
    private val elements = persistentListOf(
        apparentTemperature, fcstTime, reh, pcp, pop, tmp, tmpChart, weatherIcon, wsd
    )

    @Composable
    fun calculateHeight() = elements.sumOf {
        it.paddedHeight
    }

    @JvmInline
    value class Segment(val width: Dp)

    data class VerticalPaddingValues(
        val top: Dp = Dp.zero,
        val bottom: Dp = Dp.zero
    ) {
        fun sum() = top.plus(bottom)
    }

    sealed class Element {
        @get:Composable
        abstract val paddedHeight: Dp

        open val verticalPaddingValues: VerticalPaddingValues = VerticalPaddingValues()
    }

    abstract class Text : Element() {
        abstract val textPaint: TextPaint

        override val paddedHeight: Dp
            @Composable
            get() = with(LocalDensity.current) {
                textPaint.height.toDp()
            }
                .plus(verticalPaddingValues.sum())
    }

    data class Tmp(
        override val textPaint: TextPaint,
        override val verticalPaddingValues: VerticalPaddingValues
    ) : Text()

    data class TmpChart(
        val color: Color,
        val height: Dp
    ) : Element() {
        override val paddedHeight: Dp
            @Composable
            get() = height.plus(verticalPaddingValues.sum())

        override val verticalPaddingValues: VerticalPaddingValues
            get() = VerticalPaddingValues(4.dp, 4.dp) // TODO calculate nice value.
    }

    data class WeatherIcon(
        val size: DpSize,
        val color: Color
    ) : Element() {
        val width: Dp = size.width
        val height: Dp = size.height

        override val paddedHeight: Dp
            @Composable
            get() = height.plus(verticalPaddingValues.sum())

        override val verticalPaddingValues: VerticalPaddingValues
            get() = VerticalPaddingValues(4.dp, 4.dp) // TODO calculate nice value.
    }

    @Composable
    private fun PersistentList<Element>.sumOf(selector: @Composable (Element) -> Dp): Dp {
        var sum: Dp = Dp.zero

        for (element in this) {
            sum += selector(element)
        }

        return sum
    }

    companion object {
        fun Text(textPaint: TextPaint) = object : Text() {
            override val textPaint: TextPaint = textPaint
        }

        val defaultValue: ChartStyle
            @Composable
            get() = run {
                val labelSmall = typography.labelSmall
                    .copy(textAlign = TextAlign.Center)
                    .toTextPaint()

                val labelMedium = typography.labelMedium
                    .copy(textAlign = TextAlign.Center)
                    .toTextPaint()

                ChartStyle(
                    segment = Segment(width = 64.dp),
                    apparentTemperature = Text(labelSmall),
                    fcstTime = Text(labelSmall),
                    pcp = Text(labelMedium),
                    pop = Text(labelMedium),
                    reh = Text(labelMedium),
                    tmp = with(LocalDensity.current) {
                        Tmp(
                            textPaint = labelMedium,
                            verticalPaddingValues = VerticalPaddingValues(
                                bottom = labelMedium.height.quarter.toDp()
                            )
                        )
                    },
                    tmpChart = TmpChart(
                        color = YellowOrange,
                        height = 24.dp
                    ),
                    weatherIcon = WeatherIcon(
                        size = DpSize(width = 30.dp, height = 30.dp),
                        color = Color.Unspecified
                    ),
                    wsd = Text(labelMedium)
                )
            }
    }
}
