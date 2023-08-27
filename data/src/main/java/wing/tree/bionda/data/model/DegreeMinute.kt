package wing.tree.bionda.data.model

data class DegreeMinute(
    val degree: Int,
    val minute: Int
) {
    override fun toString(): String {
        return "$degree${String.format("%02d", minute)}"
    }
}
