package wing.tree.bionda.data.model

import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.MidLandFcst as LandFcst
import wing.tree.bionda.data.model.MidTa as Ta

sealed interface MidLandFcstTa {
    data class BothSuccess(
        val midLandFcst: LandFcst.Local,
        val midTa: Ta.Local
    ) : MidLandFcstTa

    sealed interface OneOfSuccess : MidLandFcstTa {
        data class MidLandFcst(val midLandFcst: LandFcst.Local) : OneOfSuccess
        data class MidTa(val midTa: Ta.Local) : OneOfSuccess
    }

    data class BothFailure(
        val midLandFcst: Throwable,
        val midTa: Throwable
    ) : MidLandFcstTa

    companion object {
        fun MidLandFcstTa(
            midLandFcst: Complete<LandFcst.Local>,
            midTa: Complete<Ta.Local>
        ): MidLandFcstTa {
            return when {
                midLandFcst.isSuccess() && midTa.isSuccess() -> BothSuccess(
                    midLandFcst.value,
                    midTa.value
                )

                midLandFcst.isSuccess() -> OneOfSuccess.MidLandFcst(midLandFcst.value)
                midTa.isSuccess() -> OneOfSuccess.MidTa(midTa.value)
                else -> BothFailure(
                    midLandFcst.throwable,
                    midTa.throwable
                )
            }
        }
    }
}
