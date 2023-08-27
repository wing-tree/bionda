package wing.tree.bionda.data.model

import wing.tree.bionda.data.extension.int

data class DegreeMinute(
    val degree: Int,
    val minute: Int
) {
    val int: Int get() = toString().int

    override fun toString(): String {
        return "$degree${String.format("%02d", minute)}"
    }
}
