package wing.tree.bionda.data.model

import android.icu.util.Calendar
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.model.core.State.Complete
import wing.tree.bionda.data.model.core.isSuccess
import wing.tree.bionda.data.model.MidLandFcst.Local as LandFcst
import wing.tree.bionda.data.model.MidTa.Local as Ta

sealed interface MidLandFcstTa {
    val tmFcCalendar: Calendar

    data class BothSuccess(
        override val tmFcCalendar: Calendar,
        val midLandFcst: LandFcst,
        val midTa: Ta
    ) : MidLandFcstTa {
        data class Item(
            val n: Int,
            val landFcst: LandFcst.LandFcst,
            val ta: Ta.Ta
        )

        val items: ImmutableList<Item> = midLandFcst.landFcst
            .sortedBy(LandFcst.LandFcst::n)
            .zip(midTa.ta.sortedBy(Ta.Ta::n))
            .map { (landFcst, ta) ->
                Item(n = landFcst.n, landFcst = landFcst, ta = ta)
            }.toImmutableList()

        fun advancedDayBy(n: Int) = if (n > Int.zero) {
            items.map { item ->
                with(item.n.minus(n)) {
                    item.copy(
                        n = this,
                        landFcst = item.landFcst.copy(n = this),
                        ta = item.ta.copy(n = this)
                    )
                }
            }.toImmutableList()
        } else {
            items
        }
    }

    sealed interface OneOfSuccess : MidLandFcstTa {
        val throwable: Throwable

        data class MidLandFcst(
            override val tmFcCalendar: Calendar,
            override val throwable: Throwable,
            val midLandFcst: LandFcst
        ) : OneOfSuccess

        data class MidTa(
            override val tmFcCalendar: Calendar,
            override val throwable: Throwable,
            val midTa: Ta
        ) : OneOfSuccess
    }

    data class BothFailure(
        override val tmFcCalendar: Calendar,
        val midLandFcst: Throwable,
        val midTa: Throwable
    ) : MidLandFcstTa

    companion object {
        fun MidLandFcstTa(
            midLandFcst: Complete<LandFcst>,
            midTa: Complete<Ta>,
            tmFcCalendar: Calendar
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
