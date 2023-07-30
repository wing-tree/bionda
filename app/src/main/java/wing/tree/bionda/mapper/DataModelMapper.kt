package wing.tree.bionda.mapper

interface DataModelMapper<in DM, out PM> {
    fun toPresentationModel(dataModel: DM): PM
}
