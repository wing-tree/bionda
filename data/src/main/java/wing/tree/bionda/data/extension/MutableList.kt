package wing.tree.bionda.data.extension

import timber.log.Timber

fun <T> MutableList<T>.replaceAt(index: Int, element: T): Boolean {
    return try {
        removeAt(index)
        add(index, element)
        true
    } catch (t: IndexOutOfBoundsException) {
        Timber.e(t)
        false
    }
}

fun <T> MutableList<T>.replaceFirst(element: T): Boolean = replaceAt(Int.firstIndex, element)
fun <T> MutableList<T>.updateFirst(function: (T) -> T): Boolean = replaceFirst(function(first()))
