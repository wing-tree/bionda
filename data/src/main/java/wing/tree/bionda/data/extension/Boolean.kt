package wing.tree.bionda.data.extension

inline fun Boolean?.ifTrue(then: () -> Unit) {
    if (this == true) {
        then()
    }
}
