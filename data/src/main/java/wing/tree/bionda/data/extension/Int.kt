package wing.tree.bionda.data.extension

val Int.Companion.eight: Int get() = 8
val Int.Companion.halfAnHour: Int get() = 30
val Int.Companion.one: Int get() = 1
val Int.Companion.oneHundred: Int get() = 100
val Int.Companion.sixty: Int get() = 60
val Int.Companion.ten: Int get() = 10
val Int.Companion.three: Int get() = 3
val Int.Companion.zero: Int get() = 0
val Int.half: Int get() = div(2)

fun Int.ifZero(defaultValue: () -> Int) = if (this `is` Int.zero) {
    defaultValue()
} else {
    this
}

fun Int.isZero(): Boolean = `is`(Int.zero)
