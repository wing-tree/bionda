package wing.tree.bionda.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.not
import wing.tree.bionda.data.extension.oneHundred
import wing.tree.bionda.data.model.Category
import wing.tree.bionda.data.model.CodeValue
import wing.tree.bionda.data.regular.koreaCalendar
import wing.tree.bionda.mapper.DataModelMapper
import wing.tree.bionda.data.model.forecast.Forecast as DataModel

data class Forecast(
    val items: ImmutableList<Item>
) {
    val currentItem: Item? get() = with(items) {
        firstOrNull {
            with(koreaCalendar()) {
                when  {
                    it.fcstDate not baseDate.int -> false
                    it.fcstHour not hourOfDay -> false
                    else -> true
                }
            }
        }
            ?: firstOrNull()
    }

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

    companion object : DataModelMapper<DataModel, Forecast> {
        override fun toPresentationModel(
            dataModel: DataModel
        ): Forecast {
            val items = dataModel.items.groupBy {
                it.fcstDate to it.fcstTime
            }.map { (key, value) ->
                val (fcstDate, fcstTime) = key

                Item(
                    fcstDate = fcstDate,
                    fcstTime = fcstTime,
                    codeValues = value.associate {
                        it.category to it.fcstValue
                    }
                        .toImmutableMap()
                )
            }

            return Forecast(items = items.toImmutableList())
        }
    }
}
