package wing.tree.bionda.data.extension

fun <T> Collection<T>.containsAny(elements: Collection<T>, then: () -> Unit) {
    elements.any {
        contains(it)
    }
        .ifTrue {
            then()
        }
}
