package wing.tree.bionda.data.model

import android.icu.util.Calendar
import androidx.room.Entity
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.core.Response
import wing.tree.bionda.data.exception.OpenAPIError
import wing.tree.bionda.data.extension.advanceHourOfDayBy
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.firstIndex
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.oneHundred
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
        val baseDate: String,
        val baseTime: String,
        val category: String,
        val fcstDate: String,
        val fcstTime: String,
        val fcstValue: String,
        val nx: Int,
        val ny: Int,
    ) {
        val fcstCalendar: Calendar
            get() = koreaCalendar(
                fcstDate,
                fcstTime
            )

        val hourOfDay: Int get() = fcstTime.int.div(Int.oneHundred)
    }

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
        override val items: PersistentList<Item>,
        override val nx: Int,
        override val ny: Int,
        val baseDate: String,
        val baseTime: String,
    ) : VilageFcst {
        fun prepend(vilageFcst: Local?): Local {
            val koreaCalendar = koreaCalendar.advanceHourOfDayBy(Int.one)

            return with(vilageFcst?.items ?: emptyList()) {
                // TODO make 26 to const.!
                filter {
                    // 아이템이, 2시간전보다 최신이면,
                    // 지금이 14시면, 12시 이후의 데이터들을 추출함.
                    // 첫 번째 데이터 보다, 이전의 데이터여야함.
                    println("ppppppp111:$it")
                    koreaCalendar < it.fcstCalendar &&
                            it.fcstCalendar < items.first().fcstCalendar
                }.let { elements ->
                    println("pppppppp:$elements")

                    copy(
                        items = items.mutate {
                            it.addAll(Int.firstIndex, elements)
                        }
                    )
                }
            }
        }

        fun takeAfter(`when`: Calendar): Local = copy(
            items = items.mutate {
                it.removeAll { item ->
                    when {
                        item.fcstDate > `when`.baseDate -> false
                        item.hourOfDay >= `when`.hourOfDay -> false
                        else -> true
                    }
                }
            }
        )
    }

    @Serializable
    data class Remote(
        override val response: Response<Item>,
    ) : VilageFcst, ResponseValidator<Item, Remote> {
        override val items: List<Item> get() = response.items
        override val nx: Int get() = items.firstOrNull()?.nx ?: Int.zero
        override val ny: Int get() = items.firstOrNull()?.ny ?: Int.zero

        override suspend fun validate(
            errorMsg: (Response<Item>) -> String,
            ifInvalid: (suspend (OpenAPIError) -> Remote)?,
        ): Remote {
            return validate(this, errorMsg, ifInvalid)
        }

        fun toLocal(params: VilageFcstInfoService.Params): Local = with(params) {
            Local(
                items = items.toPersistentList(),
                baseDate = baseDate,
                baseTime = baseTime,
                nx = this.nx,
                ny = this.ny
            )
        }
    }
}
