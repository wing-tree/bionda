package wing.tree.bionda.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import wing.tree.bionda.extension.zero

data class MarginValues(
    private val left: Dp = Dp.zero,
    private val top: Dp = Dp.zero,
    private val right: Dp = Dp.zero,
    private val bottom: Dp = Dp.zero
) : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) = left

    override fun calculateTopPadding() = top

    override fun calculateRightPadding(layoutDirection: LayoutDirection) = right

    override fun calculateBottomPadding() = bottom

    constructor(horizontal: Dp = Dp.zero, vertical: Dp = Dp.zero): this(
        left = horizontal,
        top = vertical,
        right = horizontal,
        bottom = vertical
    )
}
