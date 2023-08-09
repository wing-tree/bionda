package wing.tree.bionda.view.compose.composable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.drawable.VectorDrawable
import android.icu.text.SimpleDateFormat
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.collections.immutable.ImmutableList
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.isZero
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.regular.fcstCalendar
import wing.tree.bionda.extension.toTextPaint
import wing.tree.bionda.model.Forecast
import java.util.Locale

@Composable
fun Canv(
    items: ImmutableList<Forecast.Item>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val width = 64.dp
    val textPaint = typography.labelSmall.toTextPaint()
    val simpleDateFormat = SimpleDateFormat("a h시", Locale.KOREA)
    val maxTmp = items.maxOf { it.tmp?.toFloat() ?: 0F }

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
        .map {
            it.div(maxTmp)
        }

    println("zzzzzz:${tmpsToPlot},,${items.count()}")

    val path = Path()

    Canvas(modifier = modifier) {
        items.forEachIndexed { index, item ->
            val pointF = PointF(
                width.toPx() * index,
                Float.zero
            )

            val fcstCalendar = fcstCalendar(item.fcstHour)

            with(simpleDateFormat.format(fcstCalendar)) {
                drawContext.canvas.nativeCanvas.drawText(
                    this,
                    pointF.x + textPaint.shiftCenter(width.toPx(), this),
                    pointF.y,
                    textPaint
                )

                pointF.y += textPaint.height
            }

            with(item.weatherIcon) {
                pty[item.pty.code] ?: sky[item.sky.code]
            }
                ?.let {
                    val vectorDrawable = ContextCompat.getDrawable(context, it) as VectorDrawable
                    val image = vectorToBitmap(vectorDrawable)

                    drawImage(
                        image = image.asImageBitmap(),
                        topLeft = Offset(pointF.x - (image.width/ 2), pointF.y)
                    )

                    pointF.y += image.height
                }

            /** Temp grapch section **/

            pointF.x += 24.dp.toPx()

            val tmpFloat = item.tmp?.toFloat() ?: 0f
            val tmpRatio = tmpFloat.div(maxTmp)
            val sTmp = tmpRatio * 4.dp.toPx()
            val vToAdd = 1f.times(4.dp.toPx()) - sTmp // 이걸 offset에 더해준다.

            if (index.isZero()) {

            } else {

                if (index == items.lastIndex) {
                    // 추가 작업.
                }
            }

            drawContext.canvas.nativeCanvas.drawText(
                item.tmp ?: "",
                pointF.x + textPaint.shiftCenter(width.toPx(), item.tmp ?: ""),
                pointF.y,
                textPaint
            )

            /** end of temp.. */

            pointF.y += textPaint.height

            drawContext.canvas.nativeCanvas.drawText(
                item.reh ?: "",
                pointF.x + textPaint.shiftCenter(width.toPx(), item.reh ?: ""),
                pointF.y,
                textPaint
            )
        }
    }
}

fun TextPaint.shiftCenter(width: Float, text: String): Float {
    return width.minus(measureText(text)).half
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
        drawPoints(listOf(Offset(plotX, plotY)), Color.Blue, 4.dp)
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
