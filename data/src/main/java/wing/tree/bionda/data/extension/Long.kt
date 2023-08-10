package wing.tree.bionda.data.extension

val Long.Companion.five: Long get() = 5L
val Long.Companion.fiveSecondsInMilliseconds: Long get() = 5000L
val Long.Companion.negativeOne: Long get() = -1L
val Long.Companion.oneHundred: Long get() = 100L
val Long.Companion.oneSecondInMilliseconds: Long get() = 1000L
val Long.Companion.zero: Long get() = 0L
val Long.hundreds: Long get() = times(100)
val Long.int: Int get() = toInt()
