package wing.tree.bionda.view.compose.composable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.drawable.VectorDrawable
import android.icu.text.SimpleDateFormat
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import wing.tree.bionda.data.constant.CELSIUS
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.isZero
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.regular.fcstCalendar
import wing.tree.bionda.extension.drawFcstHour
import wing.tree.bionda.extension.drawReh
import wing.tree.bionda.extension.drawWeatherIcon
import wing.tree.bionda.extension.toTextPaint
import wing.tree.bionda.model.Forecast
import java.util.Locale

@Composable
fun Canv(
    items: ImmutableList<Forecast.Item>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val itemWidth = 64.dp
    val textPaint = typography.labelSmall.toTextPaint().apply {
        textAlign = Paint.Align.CENTER
    }
    val tmpTextPaint = typography.labelLarge.toTextPaint().apply {
        textAlign = Paint.Align.CENTER
    }
    val simpleDateFormat = SimpleDateFormat("a h시", Locale.KOREA)
    val maxTmp = items.maxOf { it.tmp?.toFloat() ?: 0F }

    val path = Path()

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
    ) {
        Canvas(modifier = Modifier.width(900.dp).padding()) {
            val tmpsToPlot = buildList {
                val tmps = items.map { it.tmp?.toFloat() ?: 0f }

                add(tmps.first())

                tmps.forEachIndexed { index, tmp ->
                    if (index < tmps.lastIndex) {
                        add((tmp + tmps[index.inc()]) / 2f)
                    }
                }

                add(tmps.last())
            }
                .mapIndexed { index, tmp ->
                    val tmpRatio = tmp.div(maxTmp)
                    val scalePx = 64.dp.toPx() //64가 곧 가용 범위인 것., 텍스트도 표기해야함. 그 높이까지 포함해서 실 가용범위 및 패딩지정필요.
                    val vToAdd = 1f - tmpRatio
                    Offset(itemWidth.toPx() * index, vToAdd.times(scalePx))
                }

            items.forEachIndexed { index, item ->
                val fcstCalendar = fcstCalendar(item.fcstHour)
                val pointF = PointF(
                    itemWidth.toPx() * index + itemWidth.toPx().half,
                    Float.zero
                )

                drawFcstHour(
                    fcstHour = simpleDateFormat.format(fcstCalendar),
                    pointF = pointF,
                    textPaint = textPaint
                )

                drawWeatherIcon(
                    item = item,
                    context = context,
                    pointF = pointF,
                    tint = Color(textPaint.color)
                )

                /** Temp grapch section **/

                pointF.y += 16.dp.toPx()

                val tmpStr = (item.tmp ?: "") + CELSIUS

                drawContext.canvas.nativeCanvas.drawText(
                    tmpStr,
                    pointF.x,
                    pointF.y + tmpsToPlot[index].y,
                    tmpTextPaint
                )

                val tmpsAdj = tmpsToPlot.map {
                    it.copy(y = it.y.plus(pointF.y) + 8.dp.toPx()) // 8.dp는 텍스트랑 그래프 패딩.
                }

                if (index.isZero()) {
                    val f = tmpsAdj.first()
                    path.moveTo(0f, f.y)
                } else {
                    path.apply {
                        drawQuad(
                            tmpsAdj,
                            index,
                            tmpsAdj[index],
                            false,
                            path
                        )

                        if (index == items.lastIndex) {
                            drawQuad(
                                tmpsAdj,
                                index.inc(),
                                tmpsAdj[index.inc()],
                                false,
                                path
                            )
                        }
                    }

                    drawPath(
                        path = path,
                        color = Color.Yellow,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
                pointF.y += 64.dp.toPx()
                /** end of temp.. */

                drawReh(
                    item.reh ?: String.empty,
                    pointF,
                    textPaint
                )
            }
        }
    }
}

val TextPaint.height: Float get() = with(fontMetrics) {
    descent.minus(ascent)
}

fun vectorToBitmap(vectorDrawable: VectorDrawable): Bitmap {
    val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)
    return bitmap
}

private fun DrawScope.drawQuad(
    points: List<Offset>,
    index: Int,
    item: Offset,
    showAnchorPoints: Boolean,
    path: Path
) {
    val prevX = points[index.dec()].x
    val prevY = points[index.dec()].y
    val plotX: Float
    val plotY: Float

    if (index == points.lastIndex) {
        plotX = item.x
        plotY = item.y
    } else {
        plotX = (prevX + item.x) / 2
        plotY = (prevY + item.y) / 2
    }

    if (showAnchorPoints) {
        drawPoints(listOf(Offset(plotX, plotY)), Color.Red, 6.dp)
    }
    path.quadraticBezierTo(
        prevX, prevY,
        plotX, plotY
    )
}

private fun DrawScope.drawPoints(
    points: List<Offset>,
    color: Color,
    strokeWidth: Dp
) {
    drawPoints(
        points,
        PointMode.Points,
        color,
        strokeWidth = strokeWidth.toPx(),
        cap = StrokeCap.Round
    )
}
