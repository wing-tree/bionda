package wing.tree.bionda.data.extension

import kotlinx.collections.immutable.PersistentList
import timber.log.Timber

object Builder {
    fun <T> PersistentList.Builder<T>.replaceAt(index: Int, element: T): Boolean {
        return try {
            removeAt(index)
            add(index, element)
            true
        } catch (t: IndexOutOfBoundsException) {
            Timber.e(t)
            false
        }
    }
}
