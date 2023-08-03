package wing.tree.bionda.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import wing.tree.bionda.extension.ZERO

class MarginValues(
    private val left: Dp = Dp.ZERO,
    private val top: Dp = Dp.ZERO,
    private val right: Dp = Dp.ZERO,
    private val bottom: Dp = Dp.ZERO
) : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) = left

    override fun calculateTopPadding() = top

    override fun calculateRightPadding(layoutDirection: LayoutDirection) = right

    override fun calculateBottomPadding() = bottom

    constructor(horizontal: Dp = Dp.ZERO, vertical: Dp = Dp.ZERO): this(
        left = horizontal,
        top = vertical,
        right = horizontal,
        bottom = vertical
    )
}
