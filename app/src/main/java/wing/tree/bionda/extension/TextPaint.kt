package wing.tree.bionda.extension

import android.text.TextPaint

val TextPaint.height: Float get() = with(fontMetrics) {
    descent.minus(ascent)
}
