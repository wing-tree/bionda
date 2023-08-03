package wing.tree.bionda.model

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import wing.tree.bionda.extension.isNonNegative

sealed class WindowSizeClass(val windowDpSize: DpSize) {
    abstract val marginValues: MarginValues

    class Compact(windowDpSize: DpSize) : WindowSizeClass(windowDpSize) {
        override val marginValues: MarginValues = MarginValues(horizontal = 16.dp)
    }

    class Medium(windowDpSize: DpSize) : WindowSizeClass(windowDpSize) {
        override val marginValues: MarginValues = MarginValues(horizontal = 24.dp)
    }

    class Expanded(windowDpSize: DpSize) : WindowSizeClass(windowDpSize) {
        override val marginValues: MarginValues = MarginValues(horizontal = 24.dp)
    }

    companion object {
        fun get(windowDpSize: DpSize) : WindowSizeClass {
            require(windowDpSize.width.isNonNegative)

            return when {
                windowDpSize.width < 600.dp -> Compact(windowDpSize)
                windowDpSize.width < 840.dp -> Medium(windowDpSize)
                else -> Expanded(windowDpSize)
            }
        }
    }
}
