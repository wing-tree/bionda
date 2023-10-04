package wing.tree.bionda.model

import wing.tree.bionda.data.extension.isNotNull

data class Level(
    val one: String? = null,
    val two: String? = null
) {
    fun levelDown() = when {
        two.isNotNull() -> copy(two = null)
        one.isNotNull() -> copy(one = null)
        else -> this
    }
}
