package wing.tree.bionda.model

import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.extension.isNull

data class Level(
    val one: String? = null,
    val two: String? = null
) {
    fun levelDown() = when {
        two.isNotNull() -> copy(two = null)
        one.isNotNull() -> copy(one = null)
        else -> this
    }

    fun levelUp(value: String) = when {
        one.isNull() -> copy(one = value)
        two.isNull() -> copy(two = value)
        else -> this
    }
}
