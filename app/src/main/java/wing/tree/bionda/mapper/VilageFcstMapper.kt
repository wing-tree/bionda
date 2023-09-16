package wing.tree.bionda.mapper

import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
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
                    .toPersistentMap()
            )
        }

        return PresentationModel(items = items.toPersistentList())
    }
}
