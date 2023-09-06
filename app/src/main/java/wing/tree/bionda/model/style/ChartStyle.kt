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
import wing.tree.bionda.extension.height
import wing.tree.bionda.extension.toTextPaint
import wing.tree.bionda.extension.zero
import wing.tree.bionda.theme.YellowOrange

data class ChartStyle(
    val segment: Segment,
    val apparentTemperature: ApparentTemperature,
    val fcstHour: FcstHour,
    val reh: Reh,
    val pcp: Pcp,
    val pop: Pop,
    val tmp: Tmp,
    val tmpChart: TmpChart,
    val weatherIcon: WeatherIcon
) {
    private val elements = persistentListOf(
        reh, pcp, pop, tmp, tmpChart, weatherIcon
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

    sealed class Text : Element() {
        @get:Composable
        abstract val textPaint: TextPaint

        override val paddedHeight: Dp
            @Composable
            get() = with(LocalDensity.current) {
                textPaint.height.toDp()
            }
                .plus(verticalPaddingValues.sum())
    }

    abstract class ApparentTemperature : Text() {
        companion object {
            val defaultValue = object : ApparentTemperature() {
                override val textPaint: TextPaint
                    @Composable
                    get() = typography.labelSmall
                        .copy(textAlign = TextAlign.Center)
                        .toTextPaint()
            }
        }
    }

    abstract class FcstHour : Text() {
        companion object {
            val defaultValue = object : FcstHour() {
                override val textPaint: TextPaint
                    @Composable
                    get() = typography.labelSmall
                        .copy(textAlign = TextAlign.Center)
                        .toTextPaint()
            }
        }
    }

    abstract class Pcp : Text() {
        companion object {
            val defaultValue = object : Pcp() {
                override val textPaint: TextPaint
                    @Composable
                    get() = typography.labelMedium
                        .copy(textAlign = TextAlign.Center)
                        .toTextPaint()
            }
        }
    }

    abstract class Pop : Text() {
        companion object {
            val defaultValue = object : Pop() {
                override val textPaint: TextPaint
                    @Composable
                    get() = typography.labelMedium
                        .copy(textAlign = TextAlign.Center)
                        .toTextPaint()
            }
        }
    }

    abstract class Reh : Text() {
        companion object {
            val defaultValue = object : Reh() {
                override val textPaint: TextPaint
                    @Composable
                    get() = typography.labelMedium
                        .copy(textAlign = TextAlign.Center)
                        .toTextPaint()
            }
        }
    }

    abstract class Tmp : Text() {
        companion object {
            val defaultValue = object : Tmp() {
                override val textPaint: TextPaint
                    @Composable
                    get() = typography.labelMedium
                        .copy(textAlign = TextAlign.Center)
                        .toTextPaint()
            }
        }
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
        val defaultValue = ChartStyle(
            segment = Segment(width = 64.dp),
            apparentTemperature = ApparentTemperature.defaultValue,
            fcstHour = FcstHour.defaultValue,
            pcp = Pcp.defaultValue,
            pop = Pop.defaultValue,
            reh = Reh.defaultValue,
            tmp = Tmp.defaultValue,
            tmpChart = TmpChart(
                color = YellowOrange,
                height = 24.dp
            ),
            weatherIcon = WeatherIcon(
                size = DpSize(width = 30.dp, height = 30.dp),
                color = Color.Unspecified
            )
        )
    }
}
