package wing.tree.bionda.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import wing.tree.bionda.data.extension.oneHundred
import wing.tree.bionda.data.model.Category
import wing.tree.bionda.data.model.CodeValue
import wing.tree.bionda.mapper.DataModelMapper
import wing.tree.bionda.data.model.forecast.Forecast as DataModel

data class Forecast(
    val items: ImmutableList<Item>
) {
    data class Item(
        val fcstDate: Int,
        val fcstTime: Int,
        val codeValues: ImmutableMap<String, String>
    ) {
        val fcstHour: Int get() = fcstTime.div(Int.oneHundred)
        val pty = CodeValue.Pty(
            code = codeValues[Category.VilageFcst.PTY]
        )

        val reh = codeValues[Category.VilageFcst.REH]
        val sky = CodeValue.Sky(
            code = codeValues[Category.VilageFcst.SKY]
        )

        val tmp = codeValues[Category.VilageFcst.TMP]
        val weatherIcon = WeatherIcons.Daytime
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
