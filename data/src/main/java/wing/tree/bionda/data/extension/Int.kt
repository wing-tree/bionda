@file:Suppress("unused")

package wing.tree.bionda.data.extension

val Int.Companion.eight: Int get() = 8
val Int.Companion.four: Int get() = 4
val Int.Companion.halfAnHour: Int get() = 30
val Int.Companion.negativeOne: Int get() = -1
val Int.Companion.one: Int get() = 1
val Int.Companion.oneMinute: Int get() = 60
val Int.Companion.oneHundred: Int get() = 100
val Int.Companion.single: Int get() = 1
val Int.Companion.sixty: Int get() = 60
val Int.Companion.ten: Int get() = 10
val Int.Companion.three: Int get() = 3
val Int.Companion.two: Int get() = 2
val Int.Companion.zero: Int get() = 0
val Int.dec: Int get() = dec()
val Int.float: Float get() = toFloat()
val Int.half: Int get() = div(2)
val Int.inc: Int get() = inc()
val Int.isNegative: Boolean get() = this < 0
val Int.isNonNegative: Boolean get() = this >= 0
val Int.long: Long get() = toLong()
val Int.string: String get() = toString()

fun Int.ifZero(defaultValue: () -> Int) = if (this `is` Int.zero) {
    defaultValue()
} else {
    this
}

fun Int.ifZero(defaultValue: Int) = if (this `is` Int.zero) {
    defaultValue
} else {
    this
}

fun Int.isZero(): Boolean = `is`(Int.zero)
fun Int.roundDownToTens() = times(Int.ten).div(Int.ten)
