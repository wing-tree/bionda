package wing.tree.bionda.extension

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun <T> MutableStateFlow<ImmutableSet<T>>.add(element: T) = update {
    with(it.toPersistentSet()) {
        add(element)
    }
}

fun <T> MutableStateFlow<ImmutableSet<T>>.remove(element: T) = update {
    with(it.toPersistentSet()) {
        remove(element)
    }
}

fun <T> MutableStateFlow<ImmutableSet<T>>.toggle(element: T) = update {
    with(it.toPersistentSet()) {
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
