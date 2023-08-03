package wing.tree.bionda.extension

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val Dp.Companion.ZERO: Dp get() = 0.dp
val Dp.isNonNegative: Boolean get() = this >= Dp.ZERO
val Dp.isPositive: Boolean get() = this > Dp.ZERO
