package wing.tree.bionda.data.extension

fun <T> Collection<T>.containsAny(elements: Collection<T>): Boolean {
    return elements.any {
        contains(it)
    }
}
