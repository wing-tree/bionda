package wing.tree.bionda.data.extension

inline fun <R> Boolean?.ifTrue(then: () -> R) {
    if (this == true) {
        then()
    }
}
