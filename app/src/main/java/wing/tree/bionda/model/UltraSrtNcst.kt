package wing.tree.bionda.model

import kotlinx.collections.immutable.PersistentMap
import wing.tree.bionda.data.core.Season
import wing.tree.bionda.data.core.season
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.extension.roundToOneDecimalPlace
import wing.tree.bionda.data.extension.string
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.model.Category
import wing.tree.bionda.data.model.CodeValue
import wing.tree.bionda.top.level.calculateHeatIndex
import wing.tree.bionda.top.level.calculateWindChill

data class UltraSrtNcst(
    val baseDate: String,
    val baseTime: String,
    val codeValues: PersistentMap<String, Double>,
    val tmn: String? = null,
    val tmx: String? = null
) {
    private val heatIndex: Double? get() = with(t1h) {
        if (isNotNull()) {
            calculateHeatIndex(
                ta = this,
                rh = reh ?: Double.zero
            )
                .roundToOneDecimalPlace()
        } else {
            null
        }
    }

    private val windChill: Double? get() = with(t1h) {
        if (isNotNull()) {
            calculateWindChill(
                ta = this,
                v = wsd ?: Double.zero
            )
                .roundToOneDecimalPlace()
        } else {
            null
        }
    }

    val feelsLikeTemperature: Double? get() = when(season) {
        Season.SUMMER -> heatIndex
        Season.WINTER -> windChill
    }

    val t1h = codeValues[Category.T1H]
    val rn1 = codeValues[Category.RN1]
    val uuu = codeValues[Category.UUU]
    val vvv = codeValues[Category.VVV]
    val reh = codeValues[Category.REH]
    val pty: CodeValue.Pty get() = CodeValue.Pty(code = codeValues[Category.PTY]?.code)
    val vec = codeValues[Category.VEC]
    val wsd = codeValues[Category.WSD]

    val sky = CodeValue.Sky(code = codeValues[Category.SKY]?.code)

    private val Double.code: String get() = int.string
}
