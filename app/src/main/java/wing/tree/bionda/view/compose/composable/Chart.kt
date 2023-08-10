package wing.tree.bionda.view.compose.composable

import android.graphics.PointF
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalContext
import kotlinx.collections.immutable.ImmutableList
import wing.tree.bionda.R
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.regular.fcstCalendar
import wing.tree.bionda.extension.drawFcstHour
import wing.tree.bionda.extension.drawReh
import wing.tree.bionda.extension.drawTmp
import wing.tree.bionda.extension.drawTmpChart
import wing.tree.bionda.extension.drawWeatherIcon
import wing.tree.bionda.extension.toTmpOffsets
import wing.tree.bionda.model.ChartStyle
import wing.tree.bionda.model.Forecast
import java.util.Locale

@Composable
fun Chart(
    items: ImmutableList<Forecast.Item>,
    modifier: Modifier = Modifier,
    style: ChartStyle = ChartStyle.default,
) {
    val contentColor = LocalContentColor.current
    val context = LocalContext.current
    val count = items.count()

    val segment = style.segment
    val fcstHourTextPaint = style.fcstHour.textPaint
    val tmpTextPaint = style.tmp.textPaint
    val rehTextPaint = style.reh.textPaint

    val path = Path()
    val scrollState = rememberScrollState()
    val simpleDateFormat = SimpleDateFormat(
        context.getString(R.string.pattern_fcst_hour),
        Locale.KOREA
    )

    Row(modifier = modifier.horizontalScroll(scrollState)) {
        Canvas(modifier = Modifier.width(segment.width.times(count))) {
            val tmpOffsets = items.toTmpOffsets(
                density = density,
                style = style
            )

            items.forEachIndexed { index, item ->
                val fcstCalendar = fcstCalendar(item.fcstHour)
                val pointF = PointF(
                    segment.width
                        .times(index).toPx()
                        .plus(segment.width.toPx().half),
                    Float.zero
                )

                drawFcstHour(
                    fcstHour = simpleDateFormat.format(fcstCalendar),
                    pointF = pointF,
                    textPaint = fcstHourTextPaint
                )

                drawWeatherIcon(
                    item = item,
                    context = context,
                    pointF = pointF,
                    style = with(style.weatherIcon) {
                        copy(
                            color = color.takeOrElse {
                                contentColor
                            }
                        )
                    }
                )

                drawTmp(
                    tmp = item.tmp ?: String.empty,
                    pointF = pointF,
                    offset = tmpOffsets[index],
                    textPaint = tmpTextPaint
                )

                drawTmpChart(
                    index = index,
                    tmpOffsets = tmpOffsets,
                    path = path,
                    pointF = pointF,
                    style = style.tmpChart,
                )

                drawReh(
                    reh = item.reh ?: String.empty,
                    pointF = pointF,
                    textPaint = rehTextPaint
                )
            }
        }
    }
}
