package wing.tree.bionda.data.extension

val Long.Companion.fiveSecondsInMilliseconds: Long get() = 5000L
val Long.Companion.negativeOne: Long get() = -1L
val Long.Companion.oneHundred: Long get() = 100L
val Long.Companion.oneSecondInMilliseconds: Long get() = 1000L
val Long.Companion.two: Long get() = 2L
val Long.Companion.zero: Long get() = 0L
val Long.hundreds: Long get() = times(Long.oneHundred)
val Long.int: Int get() = toInt()
