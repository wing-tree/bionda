package wing.tree.bionda.data.model

import androidx.room.Entity
import androidx.room.Ignore
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.validator.ResponseValidator

sealed interface MidLandFcst {
    val item: Item

    @Serializable
    data class Item(
        val regId: String,
        val rnSt3Am: Int?,
        val rnSt3Pm: Int?,
        val rnSt4Am: Int,
        val rnSt4Pm: Int,
        val rnSt5Am: Int,
        val rnSt5Pm: Int,
        val rnSt6Am: Int,
        val rnSt6Pm: Int,
        val rnSt7Am: Int,
        val rnSt7Pm: Int,
        val rnSt8: Int,
        val rnSt9: Int,
        val rnSt10: Int,
        val wf3Am: String,
        val wf3Pm: String,
        val wf4Am: String,
        val wf4Pm: String,
        val wf5Am: String,
        val wf5Pm: String,
        val wf6Am: String,
        val wf6Pm: String,
        val wf7Am: String,
        val wf7Pm: String,
        val wf8: String,
        val wf9: String,
        val wf10: String
    )

    @Entity("mid_land_fcst", primaryKeys = ["regId", "tmFc"])
    data class Local(
        override val item: MidLandFcst.Item,
        val regId: String = item.regId,
        val tmFc: String
    ) : MidLandFcst {
        fun prepend(midLandFcst: Local?): Local {
            midLandFcst ?: return this

            return midLandFcst.let {
                val item = item.copy(
                    rnSt3Am = it.item.rnSt3Am,
                    rnSt3Pm = it.item.rnSt3Am,
                )

                copy(item = item)
            }
        }

        data class Item(
            val n: Int,
            val rnStAm: Int?,
            val rnStPm: Int?,
            val rnSt: Int?,
            val wfAm: String?,
            val wfPm: String?,
            val wf: String?
        )

        @Ignore
        val item3 = Item(
            n = 3,
            rnStAm = item.rnSt3Am,
            rnStPm = item.rnSt3Pm,
            rnSt = null,
            wfAm = item.wf3Am,
            wfPm = item.wf3Pm,
            wf = null
        )

        @Ignore
        val item4 = Item(
            n = 4,
            rnStAm = item.rnSt4Am,
            rnStPm = item.rnSt4Pm,
            rnSt = null,
            wfAm = item.wf4Am,
            wfPm = item.wf4Pm,
            wf = null
        )

        @Ignore
        val item5 = Item(
            n = 5,
            rnStAm = item.rnSt5Am,
            rnStPm = item.rnSt5Pm,
            rnSt = null,
            wfAm = item.wf5Am,
            wfPm = item.wf5Pm,
            wf = null
        )

        @Ignore
        val item6 = Item(
            n = 6,
            rnStAm = item.rnSt6Am,
            rnStPm = item.rnSt6Pm,
            rnSt = null,
            wfAm = item.wf6Am,
            wfPm = item.wf6Pm,
            wf = null
        )

        @Ignore
        val item7 = Item(
            n = 7,
            rnStAm = item.rnSt7Am,
            rnStPm = item.rnSt7Pm,
            rnSt = null,
            wfAm = item.wf7Am,
            wfPm = item.wf7Pm,
            wf = null
        )

        @Ignore
        val item8 = Item(
            n = 8,
            rnStAm = null,
            rnStPm = null,
            rnSt = item.rnSt8,
            wfAm = null,
            wfPm = null,
            wf = item.wf8
        )

        @Ignore
        val item9 = Item(
            n = 9,
            rnStAm = null,
            rnStPm = null,
            rnSt = item.rnSt9,
            wfAm = null,
            wfPm = null,
            wf = item.wf9
        )

        @Ignore
        val item10 = Item(
            n = 10,
            rnStAm = null,
            rnStPm = null,
            rnSt = item.rnSt10,
            wfAm = null,
            wfPm = null,
            wf = item.wf10
        )

        @Ignore
        val items: ImmutableList<Item> = persistentListOf(
            item3, item4, item5, item6, item7, item8, item9, item10
        )
    }

    @Serializable
    data class Remote(
        val response: Response<Item>
    ) : MidLandFcst {
        init {
            ResponseValidator.validate(response)
        }

        override val item: Item = response.body.items.item.first()

        fun toLocal(tmFc: String) = Local(
            item = item,
            tmFc = tmFc
        )
    }
}
