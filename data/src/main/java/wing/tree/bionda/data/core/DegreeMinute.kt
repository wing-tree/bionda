package wing.tree.bionda.data.core

import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.isNegative
import wing.tree.bionda.data.extension.minusSign
import wing.tree.bionda.data.extension.three
import wing.tree.bionda.data.extension.two

data class DegreeMinute(
    val degree: Int,
    val minute: Int,
    val type: Type
) {
    override fun toString(): String {
        val sign = when {
            degree.isNegative -> String.minusSign
            else -> String.empty
        }

        return "$sign${String.format("%0${type.digits}d", degree)}${String.format("%02d", minute)}"
    }

    enum class Type(val digits: Int) {
        LATITUDE(Int.two), LONGITUDE(Int.three)
    }
}
