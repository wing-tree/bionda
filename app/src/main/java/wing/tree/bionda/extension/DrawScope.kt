package wing.tree.bionda.extension

import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.VectorDrawable
import android.text.TextPaint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.core.content.ContextCompat
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.view.compose.composable.height
import wing.tree.bionda.view.compose.composable.vectorToBitmap

fun DrawScope.drawFcstHour(
    fcstHour: String,
    pointF: PointF,
    textPaint: TextPaint
) {
    drawContext.canvas.nativeCanvas.drawText(
        fcstHour,
        pointF.x,
        pointF.y,
        textPaint
    )

    pointF.y += textPaint.height
}

fun DrawScope.drawReh(
    reh: String,
    pointF: PointF,
    textPaint: TextPaint
) {
    drawContext.canvas.nativeCanvas.drawText(
        reh,
        pointF.x,
        pointF.y,
        textPaint
    )
}

fun DrawScope.drawWeatherIcon(
    item: Forecast.Item,
    context: Context,
    pointF: PointF,
    tint: Color
) {
    with(item.weatherIcon) {
        pty[item.pty.code] ?: sky[item.sky.code]
    }
        ?.let {
            val vectorDrawable = ContextCompat.getDrawable(context, it) as VectorDrawable
            val image = vectorToBitmap(vectorDrawable)

            drawImage(
                image = image.asImageBitmap(),
                topLeft = Offset(pointF.x - image.width/2, pointF.y),
                colorFilter = ColorFilter.tint(tint, BlendMode.SrcAtop)
            )

            pointF.y += image.height
        }
}
