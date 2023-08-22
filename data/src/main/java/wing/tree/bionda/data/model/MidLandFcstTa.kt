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
        val throwable: Throwable

        data class MidLandFcst(
            override val throwable: Throwable,
            val midLandFcst: LandFcst
        ) : OneOfSuccess

        data class MidTa(
            override val throwable: Throwable,
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
                        throwable = midTa.throwable,
                        midLandFcst = midLandFcst.value
                    )
                }

                else -> when {
                    midTa.isSuccess() -> OneOfSuccess.MidTa(
                        throwable = midLandFcst.throwable,
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
