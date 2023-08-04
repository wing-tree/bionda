package wing.tree.bionda.extension

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val Dp.Companion.zero: Dp get() = 0.dp
val Dp.isNonNegative: Boolean get() = this >= Dp.zero
val Dp.isPositive: Boolean get() = this > Dp.zero
