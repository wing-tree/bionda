package wing.tree.bionda.data.extension

val String.Companion.degree: String get() = "°"
val String.Companion.empty: String get() = ""
val String.Companion.zero: String get() = "0"
val String.floatOrNull: Float? get() = toFloatOrNull()
val String.int: Int get() = toInt()

fun String.ifZero(defaultValue: () -> String) = if (this `is` String.zero) {
    defaultValue()
} else {
    this
}
