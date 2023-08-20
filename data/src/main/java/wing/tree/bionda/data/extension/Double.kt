package wing.tree.bionda.data.extension

val Double.Companion.one: Double get() = 1.0
val Double.Companion.two: Double get() = 2.0
val Double.double: Double get() = times(2.0)
val Double.half: Double get() = times(0.5)
val Double.int: Int get() = toInt()
val Double.long: Long get() = toLong()
val Double.radians: Double get() = Math.toRadians(this)
val Double.quarter: Double get() = times(0.25)

fun Double.delta(other: Double) = minus(other)
