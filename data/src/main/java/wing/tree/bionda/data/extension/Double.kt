package wing.tree.bionda.data.extension

val Double.double: Double get() = times(2)
val Double.half: Double get() = times(0.5)
val Double.int: Int get() = toInt()
val Double.quarter: Double get() = times(0.25)
