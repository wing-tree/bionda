package wing.tree.bionda.data.extension

val Float.Companion.one: Float get() = 1.0F
val Float.Companion.zero: Float get() = 0.0F
val Float.complement: Float get() = run {
    require(this in Float.zero..Float.one)

    Float.one.minus(this)
}

val Float.half: Float get() = div(2.0F)
