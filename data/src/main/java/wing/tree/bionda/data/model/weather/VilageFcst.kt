package wing.tree.bionda.data.model.weather

import androidx.room.Entity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.string
import wing.tree.bionda.data.extension.two
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.regular.koreaCalendar
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
        val response: Response<Item>
    ) : VilageFcst {
        init {
            ResponseValidator.validate(response)
        }

        override val items: List<Item> get() = response.body.items.item
        override val nx: Int = items.firstOrNull()?.nx ?: Int.zero
        override val ny: Int = items.firstOrNull()?.ny ?: Int.zero

        fun toLocal(baseDate: String, baseTime: String) = Local(
            items = items.toImmutableList(),
            baseDate = baseDate,
            baseTime = baseTime,
            nx = nx,
            ny = ny
        )
    }
}
