package wing.tree.bionda.data.model

import androidx.room.Entity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.constant.COMMA
import wing.tree.bionda.data.constant.SPACE
import wing.tree.bionda.data.exception.OpenApiError
import wing.tree.bionda.data.exception.fourth
import wing.tree.bionda.data.exception.second
import wing.tree.bionda.data.exception.third
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.string
import wing.tree.bionda.data.extension.two
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.service.VilageFcstInfoService
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.data.validator.ResponseValidator

sealed interface VilageFcst {
    val items: List<Item>
    val nx: Int
    val ny: Int

    @Serializable
    data class Item(
        val baseDate: Int,
        val baseTime: Int,
        val category: String,
        val fcstDate : Int,
        val fcstTime : Int,
        val fcstValue : String,
        val nx : Int,
        val ny : Int
    )

    @Entity(
        tableName = "vilage_fcst",
        primaryKeys = [
            "nx",
            "ny",
            "baseDate",
            "baseTime"
        ]
    )
    data class Local(
        override val items: ImmutableList<Item>,
        override val nx: Int,
        override val ny: Int,
        val baseDate: String,
        val baseTime: String
    ) : VilageFcst {
        fun prepend(vilageFcst: Local?): Local {
            val koreaCalendar = koreaCalendar().apply {
                hourOfDay -= Int.two
            }

            return with(vilageFcst?.items ?: emptyList()) {
                // TODO make 26 to const.
                takeLast(26).filter {
                    koreaCalendar < koreaCalendar(it.fcstDate.string, it.fcstTime.string)
                }.let {
                    val items = items.plus(it).toImmutableList()

                    copy(items = items)
                }
            }
        }
    }

    @Serializable
    data class Remote(
        override val response: Response<Item>
    ) : VilageFcst, ResponseValidator {
        override val items: List<Item> get() = response.items
        override val nx: Int get() = items.firstOrNull()?.nx ?: Int.zero
        override val ny: Int get() = items.firstOrNull()?.ny ?: Int.zero

        override fun validate(vararg params: String) {
            if (response.isUnsuccessful) {
                val header = response.header
                val errorCode = header.resultCode
                val errorMsg = buildList {
                    add("resultCode=${header.resultCode}")
                    add("resultMsg=${header.resultMsg}")
                    add("baseDate=${params.first()}")
                    add("baseTime=${params.second()}")
                    add("nx=${params.third()}")
                    add("ny=${params.fourth()}")
                }.joinToString("$COMMA$SPACE")

                throw OpenApiError(
                    errorCode = errorCode,
                    errorMsg = errorMsg
                )
            }
        }

        fun toLocal(params: VilageFcstInfoService.Params): Local = with(params) {
            validate(baseDate, baseTime, "$nx", "$ny")

            Local(
                items = items.toImmutableList(),
                baseDate = baseDate,
                baseTime = baseTime,
                nx = this.nx,
                ny = this.ny
            )
        }
    }
}
