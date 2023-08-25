package wing.tree.bionda.mapper

import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import wing.tree.bionda.data.model.weather.VilageFcst.Local as DataModel
import wing.tree.bionda.model.VilageFcst as PresentationModel

class VilageFcstMapper : DataModelMapper<DataModel, PresentationModel> {
    override fun toPresentationModel(dataModel: DataModel): PresentationModel {
        val items = dataModel.items.groupBy {
            it.fcstDate to it.fcstTime
        }.map { (key, value) ->
            val (fcstDate, fcstTime) = key

            wing.tree.bionda.model.VilageFcst.Item(
                fcstDate = fcstDate,
                fcstTime = fcstTime,
                codeValues = value.associate {
                    it.category to it.fcstValue
                }
                    .toImmutableMap()
            )
        }

        return PresentationModel(items = items.toImmutableList())
    }
}
