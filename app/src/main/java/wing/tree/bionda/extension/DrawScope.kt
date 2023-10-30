package wing.tree.bionda.extension

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import wing.tree.bionda.R
import wing.tree.bionda.data.extension.dec
import wing.tree.bionda.data.extension.degree
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.inc
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.isZero
import wing.tree.bionda.data.extension.quarter
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.model.style.VilageFcstStyle

val DrawScope.nativeCanvas: Canvas get() = drawContext.canvas.nativeCanvas

fun DrawScope.drawText(
    text: String,
    point: PointF,
    vilageFcstStyle: VilageFcstStyle.Text
) = with(vilageFcstStyle) {
    point.y += verticalPaddingValues.top.toPx()
    point.y += textPaint.height

    nativeCanvas.drawText(
        text,
        point.x,
        point.y,
        textPaint
    )

    point.y += verticalPaddingValues.bottom.toPx()
}

fun DrawScope.drawText(
    point: PointF,
    vilageFcstStyle: VilageFcstStyle.Text,
    draw: DrawScope.() -> Unit
) = with(vilageFcstStyle) {
    point.y += verticalPaddingValues.top.toPx()
    point.y += textPaint.height

    draw()

    point.y += verticalPaddingValues.bottom.toPx()
}

fun DrawScope.drawDay(
    day: String,
    point: PointF,
    vilageFcstStyle: VilageFcstStyle.Text
) = drawText(
    text = day,
    point = point,
    vilageFcstStyle = vilageFcstStyle
)

fun DrawScope.drawFcstTime(
    fcstTime: String,
    point: PointF,
    vilageFcstStyle: VilageFcstStyle.Text
) = drawText(
    text = fcstTime,
    point = point,
    vilageFcstStyle = vilageFcstStyle
)

fun DrawScope.drawFeelsLikeTemperature(
    feelsLikeTemperature: String,
    point: PointF,
    vilageFcstStyle: VilageFcstStyle.Text
) = drawText(
    text = feelsLikeTemperature,
    point = point,
    vilageFcstStyle = vilageFcstStyle
)

fun DrawScope.drawPcp(
    pcp: String,
    point: PointF,
    vilageFcstStyle: VilageFcstStyle.Text
) = drawText(
    text = pcp,
    point = point,
    vilageFcstStyle = vilageFcstStyle
)

fun DrawScope.drawPop(
    pop: String,
    point: PointF,
    vilageFcstStyle: VilageFcstStyle.Text
) = drawText(
    text = pop,
    point = point,
    vilageFcstStyle = vilageFcstStyle
)

fun DrawScope.drawReh(
    reh: String,
    point: PointF,
    vilageFcstStyle: VilageFcstStyle.Text
) = drawText(
    text = reh,
    point = point,
    vilageFcstStyle = vilageFcstStyle
)

fun DrawScope.drawTmp(
    tmp: String,
    point: PointF,
    offset: Offset,
    vilageFcstStyle: VilageFcstStyle.Text
) = drawText(
    point = point,
    vilageFcstStyle = vilageFcstStyle
) {
    val textPaint = vilageFcstStyle.textPaint
    val text = buildString {
        if (tmp.isNotBlank()) {
            append(tmp)
            append(String.degree)
        }
    }

    val width = textPaint.measureText(String.degree)
    val x = point.x.plus(width.half)

    nativeCanvas.drawText(
        text,
        x,
        point.y.plus(offset.y),
        textPaint
    )
}

fun DrawScope.drawTmpChart(
    index: Int,
    offsets: List<Offset>,
    path: Path,
    pointF: PointF,
    style: VilageFcstStyle.TmpChart
) {
    val height = style.height.toPx()

    if (offsets.isEmpty()) {
        pointF.y += height; return
    }

    if (index.isZero()) {
        path.moveTo(Float.zero, offsets.first().y)
    } else {
        with(path) {
            quadraticBezierTo(
                index,
                offsets
            )

            if (index `is` offsets.lastIndex.dec) {
                quadraticBezierTo(
                    index.inc,
                    offsets
                )

                drawIntoCanvas {
                    val paint = Paint().apply {
                        this.style = PaintingStyle.Stroke

                        strokeWidth = Dp.one.toPx()
                        shader = LinearGradientShader(
                            colors = listOf(
                                style.color,
                                style.color.copy(alpha = Float.half)
                            ),
                            from = Offset(Float.zero, pointF.y),
                            to = Offset(Float.zero, pointF.y.plus(style.height.toPx()))
                        )
                    }

                    it.drawPath(path, paint)
                }

                fillGradient(path, pointF, style)
            }
        }
    }

    pointF.y += with(height) {
        plus(half)
    }
}

fun DrawScope.drawWeatherIcon(
    @DrawableRes weatherIcon: Int?,
    context: Context,
    point: PointF,
    style: VilageFcstStyle.Icon
) {
    val width = style.width.toPx()
    val height = style.height.toPx()

    weatherIcon?.let {
        val image = context.getImageBitmap(
            id = it,
            width = width.int,
            height = height.int
        )

        image?.let {
            drawImage(
                image = image,
                topLeft = Offset(
                    point.x.minus(width.half),
                    point.y,
                ),
                colorFilter = ColorFilter.tint(style.color, BlendMode.SrcAtop)
            )
        }
    }

    point.y += height.half
}

fun DrawScope.drawVec(
    vec: Float?,
    context: Context,
    point: PointF,
    vilageFcstStyle: VilageFcstStyle.Icon
) {
    val width = vilageFcstStyle.width.toPx()
    val height = vilageFcstStyle.height.toPx()

    vec ?: run {
        point.y += height.half; return
    }

    context.getImageBitmap(
        id = R.drawable.wi_wind_deg,
        width = width.int,
        height = height.int
    )?.let { image ->
        rotate(vec, Offset(point.x, point.y.plus(height.half))) {
            drawImage(
                image = image,
                topLeft = Offset(
                    point.x.minus(width.half),
                    point.y,
                ),
                colorFilter = ColorFilter.tint(vilageFcstStyle.color, BlendMode.SrcAtop)
            )
        }
    }

    point.y += height.half
}

fun DrawScope.drawWsd(
    wsd: String,
    point: PointF,
    vilageFcstStyle: VilageFcstStyle.Text
) = drawText(
    text = wsd,
    point = point,
    vilageFcstStyle = vilageFcstStyle
)

fun DrawScope.fillGradient(
    path: Path,
    pointF: PointF,
    style: VilageFcstStyle.TmpChart
) {
    val height = style.height.toPx()
    val y = pointF.y.plus(height.plus(height.half))
    val fillPath = android.graphics.Path(path.asAndroidPath())
        .asComposePath()
        .apply {
            lineTo(size.width, y)
            lineTo(Float.zero, y)
            close()
        }

    val paint = Paint().apply {
        shader = LinearGradientShader(
            colors = listOf(
                style.color.copy(alpha = Float.quarter),
                Color.Transparent
            ),
            from = Offset(Float.zero, pointF.y),
            to = Offset(Float.zero, y)
        )
    }

    drawIntoCanvas {
        it.drawPath(fillPath, paint)
    }
}
