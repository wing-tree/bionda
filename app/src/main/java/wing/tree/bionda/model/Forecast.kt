package wing.tree.bionda.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import wing.tree.bionda.mapper.DataModelMapper
import wing.tree.bionda.data.model.forecast.Forecast as DataModel

data class Forecast(
    val items: ImmutableList<Item>
) {
    data class Item(
        val fcstDate: Int,
        val fcstTime: Int,
        val items: ImmutableMap<String, String>
    )

    companion object : DataModelMapper<DataModel, Forecast> {
        override fun toPresentationModel(dataModel: DataModel): Forecast {
            val items = dataModel.items.groupBy {
                it.fcstDate to it.fcstTime
            }.map { (key, value) ->
                val (fcstDate, fcstTime) = key

                Item(
                    fcstDate = fcstDate,
                    fcstTime = fcstTime,
                    items = value.associate {
                        it.category to it.fcstValue
                    }
                        .toImmutableMap()
                )
            }

            return Forecast(items = items.toImmutableList())
        }
    }
}
