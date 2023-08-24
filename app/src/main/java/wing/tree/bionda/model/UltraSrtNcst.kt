package wing.tree.bionda.model

import kotlinx.collections.immutable.ImmutableMap
import wing.tree.bionda.data.model.weather.Category

data class UltraSrtNcst(
    val baseDate: String,
    val baseTime: String,
    val codeValues: ImmutableMap<String, Double>
) {
    val t1h = codeValues[Category.T1H]
    val rn1 = codeValues[Category.RN1]
    val uuu = codeValues[Category.UUU]
    val vvv = codeValues[Category.VVV]
    val reh = codeValues[Category.REH]
    val pty = codeValues[Category.PTY]
    val vec = codeValues[Category.VEC]
    val wsd = codeValues[Category.WSD]
}
