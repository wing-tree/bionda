package wing.tree.bionda.data.model

import androidx.room.Entity
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.core.Response
import wing.tree.bionda.data.exception.OpenAPIError
import wing.tree.bionda.data.extension.advanceHourOfDayBy
import wing.tree.bionda.data.extension.two
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.service.VilageFcstInfoService
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.data.validator.ResponseValidator

sealed interface UltraSrtFcst : VilageFcst {
    @Entity(
        tableName = "ultra_srt_fcst",
        primaryKeys = [
            "nx",
            "ny",
            "baseDate",
            "baseTime",
            "minute"
        ]
    )
    data class Local(
        override val items: PersistentList<VilageFcst.Item>,
        override val nx: Int,
        override val ny: Int,
        val baseDate: String,
        val baseTime: String,
        val minute: Int
    ) : UltraSrtFcst {
        fun prepend(vilageFcst: Local?): Local {
            val koreaCalendar = koreaCalendar.advanceHourOfDayBy(Int.two)

            return with(vilageFcst?.items ?: emptyList()) {
                // TODO make 26 to const.
                takeLast(26).filter {
                    koreaCalendar < koreaCalendar(it.fcstDate, it.fcstTime)
                }.let {
                    val items = it.plus(items)

                    copy(items = items.toPersistentList())
                }
            }
        }
    }

    @Serializable
    data class Remote(
        override val response: Response<VilageFcst.Item>
    ) : UltraSrtFcst, ResponseValidator<VilageFcst.Item, Remote> {
        override val items: List<VilageFcst.Item> get() = response.items
        override val nx: Int get() = items.firstOrNull()?.nx ?: Int.zero
        override val ny: Int get() = items.firstOrNull()?.ny ?: Int.zero

        override suspend fun validate(
            errorMsg: (Response<VilageFcst.Item>) -> String,
            ifInvalid: (suspend (OpenAPIError) -> Remote)?
        ): Remote {
            return validate(this, errorMsg, ifInvalid)
        }

        fun toLocal(
            params: VilageFcstInfoService.Params,
            minute: Int
        ): Local = with(params) {
            Local(
                items = items.toPersistentList(),
                baseDate = baseDate,
                baseTime = baseTime,
                nx = nx,
                ny = ny,
                minute = minute
            )
        }
    }
}
