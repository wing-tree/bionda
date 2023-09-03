package wing.tree.bionda.mapper

import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentList
import wing.tree.bionda.model.VilageFcst
import wing.tree.bionda.data.model.VilageFcst as DataModel
import wing.tree.bionda.model.VilageFcst as PresentationModel

class VilageFcstMapper : DataModelMapper<DataModel, PresentationModel> {
    override fun toPresentationModel(dataModel: DataModel): PresentationModel {
        val items = dataModel.items.groupBy {
            it.fcstDate to it.fcstTime
        }.map { (key, value) ->
            val (fcstDate, fcstTime) = key

            PresentationModel.Item(
                fcstDate = fcstDate,
                fcstTime = fcstTime,
                codeValues = value.associate {
                    it.category to it.fcstValue
                }
                    .toImmutableMap()
            )
        }

        return PresentationModel(items = items.toPersistentList())
    }

    fun toPresentationModel(dataModel: DataModel, type: VilageFcst.Item.Type): PresentationModel {
        val items = dataModel.items.groupBy {
            it.fcstDate to it.fcstTime
        }.map { (key, value) ->
            val (fcstDate, fcstTime) = key

            PresentationModel.Item(
                fcstDate = fcstDate,
                fcstTime = fcstTime,
                codeValues = value.associate {
                    it.category to it.fcstValue
                }
                    .toImmutableMap(),
                type = type
            )
        }

        return PresentationModel(items = items.toPersistentList())
    }
}
