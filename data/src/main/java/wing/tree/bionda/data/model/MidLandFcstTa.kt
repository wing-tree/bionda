package wing.tree.bionda.data.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.MidLandFcst.Local as LandFcst
import wing.tree.bionda.data.model.MidTa.Local as Ta

sealed interface MidLandFcstTa {
    data class BothSuccess(
        val midLandFcst: LandFcst,
        val midTa: Ta
    ) : MidLandFcstTa {
        val items: ImmutableList<Pair<MidLandFcst.Local.LandFcst, Ta.Ta>> = midLandFcst.landFcsts
            .sortedBy(LandFcst.LandFcst::n)
            .zip(midTa.tas.sortedBy(Ta.Ta::n))
            .toImmutableList()
    }

    sealed interface OneOfSuccess : MidLandFcstTa {
        data class MidLandFcst(val midLandFcst: LandFcst) : OneOfSuccess
        data class MidTa(val midTa: Ta) : OneOfSuccess
    }

    data class BothFailure(
        val midLandFcst: Throwable,
        val midTa: Throwable
    ) : MidLandFcstTa

    companion object {
        fun MidLandFcstTa(
            midLandFcst: Complete<LandFcst>,
            midTa: Complete<Ta>
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
