package wing.tree.bionda.extension

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun MutableStateFlow<Boolean>.toggle() = update {
    it.not()
}
