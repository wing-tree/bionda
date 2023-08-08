package wing.tree.bionda.extension

import android.graphics.Typeface
import android.text.TextPaint
import androidx.annotation.ColorInt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.resolveAsTypeface

@Composable
fun TextStyle.toTypeface(): State<Typeface> {
    val fontFamilyResolver = LocalFontFamilyResolver.current

    return remember(fontFamilyResolver, this) {
        fontFamilyResolver.resolveAsTypeface(
            fontFamily = fontFamily,
            fontWeight = fontWeight ?: FontWeight.Normal,
            fontStyle = fontStyle ?: FontStyle.Normal,
            fontSynthesis = fontSynthesis ?: FontSynthesis.All
        )
    }
}

@Composable
fun TextStyle.toTextPaint(@ColorInt color: Int) = TextPaint().also {
    val density = LocalDensity.current
    val typeface by toTypeface()

    it.isAntiAlias = true
    it.color = color
    it.typeface = typeface
    it.textSize = with(density) {
        fontSize.toPx()
    }

    it.letterSpacing = with(density) {
        letterSpacing.toPx().div(it.textSize)
    }
}

@Composable
fun TextStyle.toTextPaint(color: Color) = TextPaint().also {
    toTextPaint(color = color.toArgb())
}
