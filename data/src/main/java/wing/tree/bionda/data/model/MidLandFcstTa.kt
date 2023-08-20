package wing.tree.bionda.data.model

data class MidLandFcstTa(
    val midLandFcst: Result.Complete<MidLandFcst.Local>,
    val midTa: Result.Complete<MidTa.Local>
)
