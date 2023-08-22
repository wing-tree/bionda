package wing.tree.bionda.data.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.calendar.TmFcCalendar
import wing.tree.bionda.data.model.MidLandFcst.Local as LandFcst
import wing.tree.bionda.data.model.MidTa.Local as Ta

sealed interface MidLandFcstTa {
    val tmFcCalendar: TmFcCalendar

    data class BothSuccess(
        override val tmFcCalendar: TmFcCalendar,
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
            override val tmFcCalendar: TmFcCalendar,
            override val throwable: Throwable,
            val midLandFcst: LandFcst
        ) : OneOfSuccess

        data class MidTa(
            override val tmFcCalendar: TmFcCalendar,
            override val throwable: Throwable,
            val midTa: Ta
        ) : OneOfSuccess
    }

    data class BothFailure(
        override val tmFcCalendar: TmFcCalendar,
        val midLandFcst: Throwable,
        val midTa: Throwable
    ) : MidLandFcstTa

    companion object {
        fun MidLandFcstTa(
            midLandFcst: Complete<LandFcst>,
            midTa: Complete<Ta>,
            tmFcCalendar: TmFcCalendar
        ): MidLandFcstTa {
            return when {
                midLandFcst.isSuccess() -> when {
                    midTa.isSuccess() -> BothSuccess(
                        tmFcCalendar = tmFcCalendar,
                        midLandFcst = midLandFcst.value,
                        midTa = midTa.value
                    )

                    else -> OneOfSuccess.MidLandFcst(
                        tmFcCalendar = tmFcCalendar,
                        throwable = midTa.throwable,
                        midLandFcst = midLandFcst.value
                    )
                }

                else -> when {
                    midTa.isSuccess() -> OneOfSuccess.MidTa(
                        tmFcCalendar = tmFcCalendar,
                        throwable = midLandFcst.throwable,
                        midTa = midTa.value
                    )

                    else -> BothFailure(
                        tmFcCalendar = tmFcCalendar,
                        midLandFcst = midLandFcst.throwable,
                        midTa = midTa.throwable
                    )
                }
            }
        }
    }
}
