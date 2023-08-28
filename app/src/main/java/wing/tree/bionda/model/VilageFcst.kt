package wing.tree.bionda.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentList
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.isNonNegative
import wing.tree.bionda.data.extension.oneHundred
import wing.tree.bionda.data.model.Category
import wing.tree.bionda.data.model.CodeValue
import wing.tree.bionda.data.model.LCRiseSetInfo
import wing.tree.bionda.top.level.emptyPersistentMap

data class VilageFcst(
    val items: PersistentList<Item>
) {
    @Stable
    data class Item(
        val fcstDate: Int,
        val fcstTime: Int,
        val codeValues: ImmutableMap<String, String>,
        val type: Type = Type.VilageFcst
    ) {
        private val weatherIcons = WeatherIcons.Daytime

        val fcstHour: Int get() = fcstTime.div(Int.oneHundred)
        val pcp = codeValues[Category.PCP]
        val pop = codeValues[Category.POP]
        val pty = CodeValue.Pty(code = codeValues[Category.PTY])
        val reh = codeValues[Category.REH]
        val sky = CodeValue.Sky(code = codeValues[Category.SKY])
        val tmp = codeValues[Category.TMP]
        val tmn = codeValues[Category.TMN]
        val tmx = codeValues[Category.TMX]
        val weatherIcon: Int? @DrawableRes get() = type.getWeatherIcon(this)
        val wsd = codeValues[Category.WSD]

        sealed interface Type {
            @DrawableRes
            fun getWeatherIcon(item: Item): Int?

            object VilageFcst : Type {
                override fun getWeatherIcon(item: Item) = with(item) {
                    weatherIcons.let {
                        it.pty[pty.code] ?: it.sky[sky.code]
                    }
                }
            }

            object Sunrise : Type {
                override fun getWeatherIcon(item: Item) = item.weatherIcons.sunrise
            }

            object Sunset : Type {
                override fun getWeatherIcon(item: Item): Int = item.weatherIcons.sunset
            }
        }
    }

    fun insertLCRiseSetInfo(lcRiseSetInfo: LCRiseSetInfo.Local): VilageFcst = with(items) {
        val builder = builder()
        val locdate = lcRiseSetInfo.locdate.trim().int
        val sunrise = lcRiseSetInfo.sunrise.trim().int
        val sunset = lcRiseSetInfo.sunset.trim().int

        indexOfFirst {
            when {
                it.fcstDate < locdate -> false
                it.fcstTime < sunrise -> false
                else -> true
            }
        }.let {
            if (it.isNonNegative) {
                val item = Item(
                    fcstDate = locdate,
                    fcstTime = sunrise,
                    codeValues = emptyPersistentMap(),
                    type = Item.Type.Sunrise
                )

                builder.add(it, item)
            }
        }

        indexOfFirst {
            when {
                it.fcstDate < locdate -> false
                it.fcstTime < sunset -> false
                else -> true
            }
        }.let {
            if (it.isNonNegative) {
                val item = Item(
                    fcstDate = locdate,
                    fcstTime = sunset,
                    codeValues = emptyPersistentMap(),
                    type = Item.Type.Sunset
                )

                builder.add(it, item)
            }
        }

        copy(items = builder.build())
    }
}
