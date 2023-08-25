package wing.tree.bionda.extension

import kotlinx.collections.immutable.PersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun <T> MutableStateFlow<PersistentSet<T>>.add(element: T) = update {
    it.add(element)
}

fun <T> MutableStateFlow<PersistentSet<T>>.remove(element: T) = update {
    it.remove(element)
}

fun <T> MutableStateFlow<PersistentSet<T>>.toggle(element: T) = update {
    with(it) {
        if (element in this) {
            remove(element)
        } else {
            add(element)
        }
    }
}

fun MutableStateFlow<Boolean>.toggle() = update {
    it.not()
}
