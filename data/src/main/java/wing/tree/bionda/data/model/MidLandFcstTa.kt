package wing.tree.bionda.data.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.core.isSuccess
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.model.MidLandFcst.Local as LandFcst
import wing.tree.bionda.data.model.MidTa.Local as Ta

sealed interface MidLandFcstTa {
    val tmFc: String

    interface AtLeastOneSuccess<T> {
        val prefix: PersistentList<T>
    }

    data class BothSuccess(
        override val tmFc: String,
        override val prefix: PersistentList<Item> = persistentListOf(),
        val julianDay: Int,
        val midLandFcst: LandFcst,
        val midTa: Ta
    ) : MidLandFcstTa, AtLeastOneSuccess<BothSuccess.Item> {
        data class Item(
            val n: Int,
            val landFcst: LandFcst.LandFcst,
            val ta: Ta.Ta
        )

        val items: PersistentList<Item> = midLandFcst.landFcst
            .sortedBy(LandFcst.LandFcst::n)
            .zip(midTa.ta.sortedBy(Ta.Ta::n))
            .map { (landFcst, ta) ->
                Item(n = landFcst.n, landFcst = landFcst, ta = ta)
            }.toPersistentList()

        fun advancedDayBy(n: Int) = if (n > Int.zero) {
            items.map { item ->
                with(item.n.minus(n)) {
                    item.copy(
                        n = this,
                        landFcst = item.landFcst.copy(n = this),
                        ta = item.ta.copy(n = this)
                    )
                }
            }
                .toPersistentList()
        } else {
            items
        }
    }

    sealed interface OneOfSuccess : MidLandFcstTa {
        val exception: Throwable

        data class MidLandFcst(
            override val tmFc: String,
            override val exception: Throwable,
            override val prefix: PersistentList<LandFcst.LandFcst> = persistentListOf(),
            val julianDay: Int,
            val midLandFcst: LandFcst
        ) : OneOfSuccess, AtLeastOneSuccess<LandFcst.LandFcst>

        data class MidTa(
            override val tmFc: String,
            override val exception: Throwable,
            override val prefix: PersistentList<Ta.Ta> = persistentListOf(),
            val julianDay: Int,
            val midTa: Ta
        ) : OneOfSuccess, AtLeastOneSuccess<Ta.Ta>
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
            tmFc: String,
            julianDay: Int
        ): MidLandFcstTa {
            return when {
                midLandFcst.isSuccess() -> when {
                    midTa.isSuccess() -> BothSuccess(
                        tmFc = tmFc,
                        julianDay = julianDay,
                        midLandFcst = midLandFcst.value,
                        midTa = midTa.value
                    )

                    else -> OneOfSuccess.MidLandFcst(
                        tmFc = tmFc,
                        julianDay = julianDay,
                        exception = midTa.exception,
                        midLandFcst = midLandFcst.value
                    )
                }

                else -> when {
                    midTa.isSuccess() -> OneOfSuccess.MidTa(
                        tmFc = tmFc,
                        julianDay = julianDay,
                        exception = midLandFcst.exception,
                        midTa = midTa.value
                    )

                    else -> BothFailure(
                        tmFc = tmFc,
                        midLandFcst = midLandFcst.exception,
                        midTa = midTa.exception
                    )
                }
            }
        }
    }
}
