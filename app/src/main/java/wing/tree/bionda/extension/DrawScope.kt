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
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.extension.isZero
import wing.tree.bionda.data.extension.quarter
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.model.style.ChartStyle

val DrawScope.nativeCanvas: Canvas get() = drawContext.canvas.nativeCanvas

fun DrawScope.drawText(
    text: String,
    point: PointF,
    chartStyle: ChartStyle.Text
) = with(chartStyle) {
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
    chartStyle: ChartStyle.Text,
    draw: DrawScope.() -> Unit
) = with(chartStyle) {
    point.y += verticalPaddingValues.top.toPx()
    point.y += textPaint.height

    draw()

    point.y += verticalPaddingValues.bottom.toPx()
}

fun DrawScope.drawFcstTime(
    fcstTime: String,
    point: PointF,
    chartStyle: ChartStyle.Text
) = drawText(
    text = fcstTime,
    point = point,
    chartStyle = chartStyle
)

fun DrawScope.drawFeelsLikeTemperature(
    feelsLikeTemperature: String,
    point: PointF,
    chartStyle: ChartStyle.Text
) = drawText(
    text = feelsLikeTemperature,
    point = point,
    chartStyle = chartStyle
)

fun DrawScope.drawPcp(
    pcp: String,
    point: PointF,
    chartStyle: ChartStyle.Text
) = drawText(
    text = pcp,
    point = point,
    chartStyle = chartStyle
)

fun DrawScope.drawPop(
    pop: String,
    point: PointF,
    chartStyle: ChartStyle.Text
) = drawText(
    text = pop,
    point = point,
    chartStyle = chartStyle
)

fun DrawScope.drawReh(
    reh: String,
    point: PointF,
    chartStyle: ChartStyle.Text
) = drawText(
    text = reh,
    point = point,
    chartStyle = chartStyle
)

fun DrawScope.drawTmp(
    tmp: String,
    point: PointF,
    offset: Offset,
    chartStyle: ChartStyle.Text
) = drawText(
    point = point,
    chartStyle = chartStyle
) {
    val textPaint = chartStyle.textPaint
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
    style: ChartStyle.TmpChart
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
    style: ChartStyle.Icon
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
    vec: Float,
    context: Context,
    point: PointF,
    chartStyle: ChartStyle.Icon
) {
    val width = chartStyle.width.toPx()
    val height = chartStyle.height.toPx()

    val image = context.getImageBitmap(
        id = R.drawable.wi_wind_deg,
        width = width.int,
        height = height.int
    )

    if (image.isNotNull()) {
        rotate(vec, Offset(
            point.x,
            point.y.plus(height.half)
        )) {
            drawImage(
                image = image,
                topLeft = Offset(
                    point.x.minus(width.half),
                    point.y,
                ),
                colorFilter = ColorFilter.tint(chartStyle.color, BlendMode.SrcAtop)
            )
        }
    }

    point.y += height.half
}

fun DrawScope.drawWsd(
    wsd: String,
    point: PointF,
    chartStyle: ChartStyle.Text
) = drawText(
    text = wsd,
    point = point,
    chartStyle = chartStyle
)

fun DrawScope.fillGradient(
    path: Path,
    pointF: PointF,
    style: ChartStyle.TmpChart
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
