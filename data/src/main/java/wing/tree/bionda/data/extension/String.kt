package wing.tree.bionda.data.extension

val String.Companion.empty: String get() = ""
val String.Companion.zero: String get() = "0"

fun String.ifZero(defaultValue: () -> String) = if (this `is` String.zero) {
    defaultValue()
} else {
    this
}
