package wing.tree.bionda.data.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import wing.tree.bionda.data.constant.PATTERN_DT_FC
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.MidLandFcst.Local as LandFcst
import wing.tree.bionda.data.model.MidTa.Local as Ta

sealed interface MidLandFcstTa {
    val tmFc: String
    val dtFc: Int get() = tmFc.take(PATTERN_DT_FC.length).int

    data class BothSuccess(
        override val tmFc: String,
        val midLandFcst: LandFcst,
        val midTa: Ta
    ) : MidLandFcstTa {
        val items: ImmutableList<Pair<LandFcst.LandFcst, Ta.Ta>> = midLandFcst.landFcst
            .sortedBy(LandFcst.LandFcst::n)
            .zip(midTa.ta.sortedBy(Ta.Ta::n))
            .toImmutableList()
    }

    sealed interface OneOfSuccess : MidLandFcstTa {
        val throwable: Throwable

        data class MidLandFcst(
            override val tmFc: String,
            override val throwable: Throwable,
            val midLandFcst: LandFcst
        ) : OneOfSuccess

        data class MidTa(
            override val tmFc: String,
            override val throwable: Throwable,
            val midTa: Ta
        ) : OneOfSuccess
    }

    data class BothFailure(
        override val tmFc: String,
        val midLandFcst: Throwable,
        val midTa: Throwable
    ) : MidLandFcstTa

    companion object {
        fun MidLandFcstTa(
            midLandFcst: Complete<LandFcst>,
            midTa: Complete<Ta>,
            tmFc: String
        ): MidLandFcstTa {
            return when {
                midLandFcst.isSuccess() -> when {
                    midTa.isSuccess() -> BothSuccess(
                        tmFc = tmFc,
                        midLandFcst = midLandFcst.value,
                        midTa = midTa.value
                    )

                    else -> OneOfSuccess.MidLandFcst(
                        tmFc = tmFc,
                        throwable = midTa.throwable,
                        midLandFcst = midLandFcst.value
                    )
                }

                else -> when {
                    midTa.isSuccess() -> OneOfSuccess.MidTa(
                        tmFc = tmFc,
                        throwable = midLandFcst.throwable,
                        midTa = midTa.value
                    )

                    else -> BothFailure(
                        tmFc = tmFc,
                        midLandFcst = midLandFcst.throwable,
                        midTa = midTa.throwable
                    )
                }
            }
        }
    }
}
