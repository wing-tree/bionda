package wing.tree.bionda.data.extension

inline fun Boolean?.ifTrue(block: () -> Unit) {
    if (this == true) {
        block()
    }

}