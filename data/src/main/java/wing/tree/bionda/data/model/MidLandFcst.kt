package wing.tree.bionda.data.model

import androidx.room.Entity
import androidx.room.Ignore
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.constant.COMMA
import wing.tree.bionda.data.constant.SPACE
import wing.tree.bionda.data.exception.OpenApiError
import wing.tree.bionda.data.exception.second
import wing.tree.bionda.data.extension.zero
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
        override val item: Item,
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

        data class LandFcst(
            val n: Int,
            val rnStAm: Int?,
            val rnStPm: Int?,
            val rnSt: Int?,
            val wfAm: String?,
            val wfPm: String?,
            val wf: String?
        )

        @Ignore
        val landFcst3 = LandFcst(
            n = 3,
            rnStAm = item.rnSt3Am,
            rnStPm = item.rnSt3Pm,
            rnSt = null,
            wfAm = item.wf3Am,
            wfPm = item.wf3Pm,
            wf = null
        )

        @Ignore
        val landFcst4 = LandFcst(
            n = 4,
            rnStAm = item.rnSt4Am,
            rnStPm = item.rnSt4Pm,
            rnSt = null,
            wfAm = item.wf4Am,
            wfPm = item.wf4Pm,
            wf = null
        )

        @Ignore
        val landFcst5 = LandFcst(
            n = 5,
            rnStAm = item.rnSt5Am,
            rnStPm = item.rnSt5Pm,
            rnSt = null,
            wfAm = item.wf5Am,
            wfPm = item.wf5Pm,
            wf = null
        )

        @Ignore
        val landFcst6 = LandFcst(
            n = 6,
            rnStAm = item.rnSt6Am,
            rnStPm = item.rnSt6Pm,
            rnSt = null,
            wfAm = item.wf6Am,
            wfPm = item.wf6Pm,
            wf = null
        )

        @Ignore
        val landFcst7 = LandFcst(
            n = 7,
            rnStAm = item.rnSt7Am,
            rnStPm = item.rnSt7Pm,
            rnSt = null,
            wfAm = item.wf7Am,
            wfPm = item.wf7Pm,
            wf = null
        )

        @Ignore
        val landFcst8 = LandFcst(
            n = 8,
            rnStAm = null,
            rnStPm = null,
            rnSt = item.rnSt8,
            wfAm = null,
            wfPm = null,
            wf = item.wf8
        )

        @Ignore
        val landFcst9 = LandFcst(
            n = 9,
            rnStAm = null,
            rnStPm = null,
            rnSt = item.rnSt9,
            wfAm = null,
            wfPm = null,
            wf = item.wf9
        )

        @Ignore
        val landFcst10 = LandFcst(
            n = 10,
            rnStAm = null,
            rnStPm = null,
            rnSt = item.rnSt10,
            wfAm = null,
            wfPm = null,
            wf = item.wf10
        )

        @Ignore
        val landFcst: ImmutableList<LandFcst> = persistentListOf(
            landFcst3, landFcst4, landFcst5, landFcst6, landFcst7, landFcst8, landFcst9, landFcst10
        )

        fun advancedDayBy(n: Int) = if (n > Int.zero) {
            landFcst.map {
                it.copy(n = it.n.minus(n))
            }.toImmutableList()
        } else {
            landFcst
        }
    }

    @Serializable
    data class Remote(
        override val response: Response<Item>
    ) : MidLandFcst, ResponseValidator {
        override val item: Item get() = response.body.items.item.first()

        override fun validate(vararg params: String) {
            if (response.isUnsuccessful) {
                val header = response.header
                val errorCode = header.resultCode
                val errorMsg = buildList {
                    add("resultCode=${header.resultCode}")
                    add("resultMsg=${header.resultMsg}")
                    add("regId=${params.first()}")
                    add("tmFc=${params.second()}")
                }
                    .joinToString("$COMMA$SPACE")

                throw OpenApiError(
                    errorCode = errorCode,
                    errorMsg = errorMsg
                )
            }
        }

        fun toLocal(regId: String, tmFc: String): Local {
            validate(regId, tmFc)

            return Local(
                item = item,
                tmFc = tmFc
            )
        }
    }
}
