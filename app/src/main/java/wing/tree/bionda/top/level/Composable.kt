package wing.tree.bionda.top.level

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberMutableInteractionSource() = remember {
    MutableInteractionSource()
}
