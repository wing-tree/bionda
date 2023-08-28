package wing.tree.bionda.data.model

sealed interface CodeValue {
    val code: String?
    val value: String?

    data class Pty(
        override val code: String?,
        override val value: String? = pty[code]
    ) : CodeValue {
        val rain: Boolean get() = code in listOf(
            "1", "2", "5", "6"
        )

        val snow: Boolean get() = code in listOf(
            "3", "6", "7"
        )
    }

    data class Sky(
        override val code: String?,
        override val value: String? = sky[code]
    ) : CodeValue

    companion object {
        private val pty = mapOf(
            "0" to null,
            "1" to "비",
            "2" to "비/눈",
            "3" to "눈",
            "4" to "소나기",
            "5" to "빗방울",
            "6" to "빗방울눈날림",
            "7" to "눈날림"
        )

        private val sky = mapOf(
            "1" to "맑음",
            "3" to "구름많음",
            "4" to "흐림"
        )
    }
}
