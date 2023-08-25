package wing.tree.bionda.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import wing.tree.bionda.data.extension.oneHundred
import wing.tree.bionda.data.model.weather.Category
import wing.tree.bionda.data.model.weather.CodeValue

data class VilageFcst(
    val items: ImmutableList<Item>
) {
    @Stable
    data class Item(
        val fcstDate: Int,
        val fcstTime: Int,
        val codeValues: ImmutableMap<String, String>
    ) {
        val fcstHour: Int get() = fcstTime.div(Int.oneHundred)
        val pcp = codeValues[Category.PCP]
        val pop = codeValues[Category.POP]
        val pty = CodeValue.Pty(code = codeValues[Category.PTY])
        val reh = codeValues[Category.REH]
        val sky = CodeValue.Sky(code = codeValues[Category.SKY])
        val tmp = codeValues[Category.TMP]
        val tmn = codeValues[Category.TMN]
        val tmx = codeValues[Category.TMX]
        val weatherIcon = WeatherIcons.Daytime
        val wsd = codeValues[Category.WSD]
    }
}
