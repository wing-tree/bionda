package wing.tree.bionda.data.core

object HangulJamo {
    private const val HANGUL_SYLLABLES_START = 44032

    private const val MEDIAL_COUNT = 21
    private const val FINAL_CONSONANT_COUNT = 28

    private val initialConsonants = arrayOf(
        "ㄱ", "ㄲ", "ㄴ", "ㄷ",
        "ㄸ", "ㄹ", "ㅁ", "ㅂ",
        "ㅃ", "ㅅ", "ㅆ", "ㅇ",
        "ㅈ", "ㅉ", "ㅊ", "ㅋ",
        "ㅌ", "ㅍ", "ㅎ"
    )

    private val medial = arrayOf(
        "ㅏ", "ㅐ", "ㅑ", "ㅒ",
        "ㅓ", "ㅔ", "ㅕ", "ㅖ",
        "ㅗ", "ㅘ", "ㅙ", "ㅚ",
        "ㅛ", "ㅜ", "ㅝ", "ㅞ",
        "ㅟ", "ㅠ", "ㅡ", "ㅢ",
        "ㅣ"
    )

    private val finalConsonants = arrayOf(
        "", "ㄱ", "ㄲ", "ㄳ",
        "ㄴ", "ㄵ", "ㄶ", "ㄷ",
        "ㄹ", "ㄺ", "ㄻ", "ㄼ",
        "ㄽ", "ㄾ", "ㄿ", "ㅀ",
        "ㅁ", "ㅂ", "ㅄ", "ㅅ",
        "ㅆ", "ㅇ", "ㅈ", "ㅊ",
        "ㅋ", "ㅌ", "ㅍ", "ㅎ"
    )

    val consonants = arrayOf(
        "ㄱ", "ㄲ", "ㄳ", "ㄴ",
        "ㄵ", "ㄶ", "ㄷ", "ㄸ",
        "ㄹ", "ㄺ", "ㄻ", "ㄼ",
        "ㄽ", "ㄾ", "ㄿ", "ㅀ",
        "ㅁ", "ㅂ", "ㅄ", "ㅃ",
        "ㅅ", "ㅆ", "ㅇ", "ㅈ",
        "ㅉ", "ㅊ", "ㅋ", "ㅌ",
        "ㅍ", "ㅎ"
    )

    val String.jamo: String
        get() = buildString {
            this@jamo.forEach {
                if(it.code >= HANGUL_SYLLABLES_START) {
                    with(it.code.minus(HANGUL_SYLLABLES_START)) {
                        append(initialConsonants[div(FINAL_CONSONANT_COUNT).div(MEDIAL_COUNT)])
                        append(medial[div(FINAL_CONSONANT_COUNT).mod(MEDIAL_COUNT)])
                        append(finalConsonants[mod(FINAL_CONSONANT_COUNT)])
                    }
                } else {
                    append(it)
                }
            }
        }
}
