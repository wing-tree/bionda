package wing.tree.bionda.data.extension

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun <T> ImmutableList<T>.toggle(element: T): ImmutableList<T> = toMutableList()
    .apply {
        if (remove(element).not()) {
            add(element)
        }
    }.toImmutableList()
