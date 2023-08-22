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
        val items: ImmutableList<Pair<MidLandFcst.Local.LandFcst, Ta.Ta>> = midLandFcst.landFcst
            .sortedBy(LandFcst.LandFcst::n)
            .zip(midTa.ta.sortedBy(Ta.Ta::n))
            .toImmutableList()
    }

    sealed interface OneOfSuccess : MidLandFcstTa {
        val error: Throwable

        data class MidLandFcst(
            override val error: Throwable,
            val midLandFcst: LandFcst
        ) : OneOfSuccess

        data class MidTa(
            override val error: Throwable,
            val midTa: Ta
        ) : OneOfSuccess
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
                midLandFcst.isSuccess() -> when {
                    midTa.isSuccess() -> BothSuccess(
                        midLandFcst.value,
                        midTa.value
                    )

                    else -> OneOfSuccess.MidLandFcst(
                        error = midTa.throwable,
                        midLandFcst = midLandFcst.value
                    )
                }

                else -> when {
                    midTa.isSuccess() -> OneOfSuccess.MidTa(
                        error = midLandFcst.throwable,
                        midTa = midTa.value
                    )

                    else -> BothFailure(
                        midLandFcst.throwable,
                        midTa.throwable
                    )
                }
            }
        }
    }
}
