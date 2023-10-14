package wing.tree.bionda.extension

import androidx.compose.runtime.MutableState

fun MutableState<Boolean>.toggle() {
    value = value.not()
}
