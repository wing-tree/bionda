package wing.tree.bionda.data.model.weather

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
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.validator.ResponseValidator

sealed interface UltraSrtNcst {
    val items: List<Item>
    val nx: Int
    val ny: Int

    @Serializable
    data class Item(
        val baseDate: Int,
        val baseTime: Int,
        val category: String,
        val nx: Int,
        val ny: Int,
        val obsrValue: Double
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
    ) : UltraSrtNcst

    @Serializable
    data class Remote(
        override val response: Response<Item>
    ) : UltraSrtNcst, ResponseValidator {
        override val items: List<Item> get() = response.body.items.item
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

        fun toLocal(
            baseDate: String,
            baseTime: String,
            nx: Int,
            ny: Int
        ): Local {
            validate(baseDate, baseTime, "$nx", "$ny")

            return Local(
                items = items.toImmutableList(),
                baseDate = baseDate,
                baseTime = baseTime,
                nx = this.nx,
                ny = this.ny
            )
        }
    }
}