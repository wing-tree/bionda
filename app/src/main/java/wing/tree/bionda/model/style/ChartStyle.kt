package wing.tree.bionda.model.style

import android.text.TextPaint
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
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

data class ChartStyle(
    val segment: Segment,
    val day: Text,
    val fcstTime: Text,
    val feelsLikeTemperature: Text,
    val reh: Text,
    val pcp: Text,
    val pop: Text,
    val tmp: Text,
    val tmpChart: TmpChart,
    val weatherIcon: Icon,
    val vec: Icon,
    val wsd: Text
) {
    private val elements = persistentListOf(
        day, fcstTime, feelsLikeTemperature,
        reh, pcp, pop, tmp, tmpChart, vec,
        weatherIcon, wsd
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

    abstract class Icon : Element() {
        abstract val color: Color
        abstract val size: DpSize

        override val paddedHeight: Dp
            @Composable
            get() = size.height.plus(verticalPaddingValues.sum())

        val width: Dp get() = size.width
        val height: Dp get() = size.height
    }

    abstract class Text(
        override val verticalPaddingValues: VerticalPaddingValues = VerticalPaddingValues()
    ) : Element() {
        abstract val textPaint: TextPaint

        override val paddedHeight: Dp
            @Composable
            get() = with(LocalDensity.current) {
                textPaint.height.toDp()
            }
                .plus(verticalPaddingValues.sum())
    }

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

    @Composable
    private fun PersistentList<Element>.sumOf(selector: @Composable (Element) -> Dp): Dp {
        var sum: Dp = Dp.zero

        for (element in this) {
            sum += selector(element)
        }

        return sum
    }

    companion object {
        private fun Icon(
            color: Color,
            size: DpSize
        ) = object : Icon() {
            override val color = color
            override val size = size
        }

        private fun Text(
            textPaint: TextPaint,
            verticalPaddingValues: VerticalPaddingValues = VerticalPaddingValues()
        ) = object : Text() {
            override val textPaint: TextPaint = textPaint
            override val verticalPaddingValues: VerticalPaddingValues = verticalPaddingValues
        }

        val defaultValue: ChartStyle
            @Composable
            get() = run {
                val contentColor = LocalContentColor.current
                val labelSmall = typography.labelSmall
                    .copy(textAlign = TextAlign.Center)
                    .toTextPaint()

                val labelMedium = typography.labelMedium
                    .copy(textAlign = TextAlign.Center)
                    .toTextPaint()

                ChartStyle(
                    segment = Segment(width = 56.dp),
                    day = Text(labelSmall),
                    feelsLikeTemperature = Text(labelSmall),
                    fcstTime = Text(labelSmall),
                    pcp = Text(labelMedium),
                    pop = Text(labelMedium),
                    reh = Text(labelMedium),
                    tmp = with(LocalDensity.current) {
                        Text(
                            textPaint = labelMedium,
                            verticalPaddingValues = VerticalPaddingValues(
                                bottom = labelMedium.height.quarter.toDp()
                            )
                        )
                    },
                    tmpChart = TmpChart(
                        color = colorScheme.primary,
                        height = 24.dp
                    ),
                    vec = Icon(
                        color = contentColor,
                        size = DpSize(width = 18.dp, height = 18.dp)
                    ),
                    weatherIcon = Icon(
                        color = contentColor,
                        size = DpSize(width = 30.dp, height = 30.dp),
                    ),
                    wsd = Text(labelMedium)
                )
            }
    }
}
