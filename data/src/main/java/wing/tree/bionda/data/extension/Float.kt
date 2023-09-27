@file:Suppress("unused")

package wing.tree.bionda.data.extension

val Float.Companion.full: Float get() = Float.one
val Float.Companion.half: Float get() = 0.5F
val Float.Companion.one: Float get() = 1.0F
val Float.Companion.onePercent: Float get() = 0.01F
val Float.Companion.quarter: Float get() = 0.25F
val Float.Companion.thirty: Float get() = 30F
val Float.Companion.threeQuarters: Float get() = 0.75F
val Float.Companion.zero: Float get() = 0.0F
val Float.complement: Float get() = run {
    require(this in Float.zero..Float.one)

    Float.one.minus(this)
}

val Float.half: Float get() = div(2.0F)
val Float.int: Int get() = toInt()
val Float.quarter: Float get() = div(4.0F)

fun Float.isNotZero(): Boolean = not(Float.zero)
fun Float.ifZero(defaultValue: () -> Float) = if (this `is` Float.zero) {
    defaultValue()
} else {
    this
}

fun Float.isZero(): Boolean = `is`(Float.zero)
