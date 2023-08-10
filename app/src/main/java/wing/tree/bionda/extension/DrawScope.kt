package wing.tree.bionda.extension

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.text.TextPaint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import wing.tree.bionda.data.constant.CELSIUS
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.isZero
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.model.ChartStyle
import wing.tree.bionda.model.Forecast

val DrawScope.nativeCanvas: Canvas get() = drawContext.canvas.nativeCanvas

fun DrawScope.drawFcstHour(
    fcstHour: String,
    pointF: PointF,
    textPaint: TextPaint
) {
    nativeCanvas.drawText(
        fcstHour,
        pointF.x,
        pointF.y,
        textPaint
    )
}

fun DrawScope.drawReh(
    reh: String,
    pointF: PointF,
    textPaint: TextPaint
) {
    nativeCanvas.drawText(
        reh,
        pointF.x,
        pointF.y,
        textPaint
    )
}

fun DrawScope.drawTmp(
    tmp: String,
    pointF: PointF,
    offset: Offset,
    textPaint: TextPaint
) {
    val text = buildString {
        if (tmp.isNotBlank()) {
            append(tmp)
            append(CELSIUS)
        }
    }

    nativeCanvas.drawText(
        text,
        pointF.x,
        pointF.y.plus(offset.y),
        textPaint
    )
}

fun DrawScope.drawTmpChart(
    index: Int,
    tmpOffsets: List<Offset>,
    path: Path,
    pointF: PointF,
    style: ChartStyle.TmpChart
) {
    val height = style.height.toPx()
    val offsets = tmpOffsets.map {
        it.copy(y = it.y.plus(pointF.y))
    }

    if (index.isZero()) {
        path.moveTo(Float.zero, offsets.first().y)
    } else {
        path.apply {
            quadraticBezierTo(
                index,
                offsets
            )

            if (index `is` offsets.lastIndex.dec()) {
                quadraticBezierTo(
                    index.inc(),
                    offsets
                )

                drawPath(
                    path = path,
                    color = style.color,
                    style = Stroke(width = Dp.one.toPx())
                )

                val fillPath = android.graphics.Path(path.asAndroidPath())
                    .asComposePath()
                    .apply {
                        lineTo(size.width, pointF.y.plus(height))
                        lineTo(Float.zero, pointF.y.plus(height))
                        close()
                    }

                val paint = Paint().apply {
                    shader = LinearGradientShader(
                        colors = listOf(
                            style.color.copy(alpha = Float.half),
                            Color.Transparent,
                        ),
                        from = Offset(Float.zero, pointF.y),
                        to = Offset(Float.zero, pointF.y.plus(height))
                    )
                }

                drawIntoCanvas {
                    it.drawPath(fillPath, paint)
                }
            }
        }
    }

    pointF.y += height
}

fun DrawScope.drawWeatherIcon(
    item: Forecast.Item,
    context: Context,
    pointF: PointF,
    style: ChartStyle.WeatherIcon
) {
    val width = style.width.toPx()
    val height = style.height.toPx()

    with(item.weatherIcon) {
        pty[item.pty.code] ?: sky[item.sky.code]
    }
        ?.let {
            val image = ContextCompat.getDrawable(context, it)
                ?.toBitmap(width = width.int, height = height.int)
                ?.asImageBitmap()

            image?.let {
                drawImage(
                    image = image,
                    topLeft = Offset(pointF.x.minus(image.width.half), pointF.y),
                    colorFilter = ColorFilter.tint(style.color, BlendMode.SrcAtop)
                )
            }
        }

    pointF.y += with(height) {
        plus(half)
    }
}
