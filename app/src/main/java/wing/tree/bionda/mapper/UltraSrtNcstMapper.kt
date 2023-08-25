package wing.tree.bionda.mapper

import kotlinx.collections.immutable.toImmutableMap
import wing.tree.bionda.model.UltraSrtNcst as PresentationModel
import wing.tree.bionda.data.model.weather.UltraSrtNcst.Local as DataModel

class UltraSrtNcstMapper : DataModelMapper<DataModel, PresentationModel> {
    override fun toPresentationModel(dataModel: DataModel): PresentationModel = with(dataModel) {
        PresentationModel(
            baseDate = baseDate,
            baseTime = baseTime,
            codeValues = items.associate {
                it.category to it.obsrValue
            }
                .toImmutableMap()
        )
    }
}
