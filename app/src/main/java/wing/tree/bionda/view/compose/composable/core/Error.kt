package wing.tree.bionda.view.compose.composable.core

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Error(
    exception: Throwable,
    modifier: Modifier = Modifier
) {
    Text(text = exception.message ?: "$exception")
}
