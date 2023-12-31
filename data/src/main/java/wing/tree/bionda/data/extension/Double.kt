@file:Suppress("unused")

package wing.tree.bionda.data.extension

import wing.tree.bionda.data.core.DegreeMinute
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sign

val Double.Companion.one: Double get() = 1.0
val Double.Companion.ten: Double get() = 10.0
val Double.Companion.two: Double get() = 2.0
val Double.Companion.zero: Double get() = 0.0

val Double.double: Double get() = times(2.0)
val Double.half: Double get() = times(0.5)
val Double.int: Int get() = toInt()
val Double.isNegative: Boolean get() = this < Double.zero
val Double.isZero: Boolean get() = this `is` Double.zero
val Double.long: Long get() = toLong()
val Double.radians: Double get() = Math.toRadians(this)
val Double.quarter: Double get() = times(0.25)
val Double.string: String get() = toString()

fun Double.delta(other: Double) = minus(other)
fun Double.roundToOneDecimalPlace(): Double {
    return round(times(Int.ten)).div(Int.ten)
}

fun Double.toDegreeMinute(type: DegreeMinute.Type): DegreeMinute {
    val abs = abs(this)

    var degree = abs.int
    var minute = abs.minus(degree).times(Int.oneMinute)

    if (minute >= Int.oneMinute) {
        degree += Int.one
        minute -= Int.oneMinute
    }

    return DegreeMinute(
        degree = sign.int.times(degree),
        minute = minute.int,
        type = type
    )
}
