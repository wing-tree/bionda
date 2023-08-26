package wing.tree.bionda.data.extension

import wing.tree.bionda.data.model.DegreeMinute

val Double.Companion.one: Double get() = 1.0
val Double.Companion.two: Double get() = 2.0
val Double.degreeMinute: DegreeMinute get() = run {
    val degree = int
    val minute = minus(degree).times(60).int

    DegreeMinute(degree = degree, minute = minute)
}

val Double.double: Double get() = times(2.0)
val Double.half: Double get() = times(0.5)
val Double.int: Int get() = toInt()
val Double.long: Long get() = toLong()
val Double.radians: Double get() = Math.toRadians(this)
val Double.quarter: Double get() = times(0.25)

fun Double.delta(other: Double) = minus(other)
