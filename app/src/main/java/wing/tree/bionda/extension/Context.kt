package wing.tree.bionda.extension

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap

fun Context.getImageBitmap(
    @DrawableRes id: Int,
    @Px width: Int,
    @Px height: Int
): ImageBitmap? = ContextCompat.getDrawable(this, id)
    ?.toBitmap(width = width, height = height)
    ?.asImageBitmap()
