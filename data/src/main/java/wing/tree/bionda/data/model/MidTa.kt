package wing.tree.bionda.data.model

import androidx.room.Entity
import androidx.room.Ignore
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.core.Response
import wing.tree.bionda.data.exception.OpenAPIError
import wing.tree.bionda.data.extension.negativeOne
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.validator.ResponseValidator

sealed interface MidTa {
    val item: Item

    @Serializable
    data class Item(
        val regId: String,
        val taMin3: Int?,
        val taMin3Low: Int?,
        val taMin3High: Int?,
        val taMax3: Int?,
        val taMax3Low: Int?,
        val taMax3High: Int?,
        val taMin4: Int,
        val taMin4Low: Int,
        val taMin4High: Int,
        val taMax4: Int,
        val taMax4Low: Int,
        val taMax4High: Int,
        val taMin5: Int,
        val taMin5Low: Int,
        val taMin5High: Int,
        val taMax5: Int,
        val taMax5Low: Int,
        val taMax5High: Int,
        val taMin6: Int,
        val taMin6Low: Int,
        val taMin6High: Int,
        val taMax6: Int,
        val taMax6Low: Int,
        val taMax6High: Int,
        val taMin7: Int,
        val taMin7Low: Int,
        val taMin7High: Int,
        val taMax7: Int,
        val taMax7Low: Int,
        val taMax7High: Int,
        val taMin8: Int,
        val taMin8Low: Int,
        val taMin8High: Int,
        val taMax8: Int,
        val taMax8Low: Int,
        val taMax8High: Int,
        val taMin9: Int,
        val taMin9Low: Int,
        val taMin9High: Int,
        val taMax9: Int,
        val taMax9Low: Int,
        val taMax9High: Int,
        val taMin10: Int,
        val taMin10Low: Int,
        val taMin10High: Int,
        val taMax10: Int,
        val taMax10Low: Int,
        val taMax10High: Int
    )

    @Entity(tableName = "mid_ta", primaryKeys = ["regId", "tmFc"])
    data class Local(
        override val item: Item,
        val regId: String = item.regId,
        val tmFc: String
    ) : MidTa {
        fun prepend(midTa: Local?): Local {
            midTa ?: return this

            val item = with(midTa.ta3) {
                item.copy(
                    taMin3 = min,
                    taMin3Low = minLow,
                    taMin3High = minHigh,
                    taMax3 = max,
                    taMax3Low = maxLow,
                    taMax3High = maxHigh
                )
            }

            return copy(item = item)
        }

        data class Ta(
            val min: Int,
            val minLow: Int,
            val minHigh: Int,
            val max: Int,
            val maxLow: Int,
            val maxHigh: Int,
            val n: Int
        )

        @Ignore
        val ta3 = Ta(
            min = item.taMin3 ?: Int.negativeOne,
            minLow = item.taMin3Low ?: Int.negativeOne,
            minHigh = item.taMin3High ?: Int.negativeOne,
            max = item.taMax3 ?: Int.negativeOne,
            maxLow = item.taMax3Low ?: Int.negativeOne,
            maxHigh = item.taMax3High ?: Int.negativeOne,
            n = 3
        )

        @Ignore
        val ta4 = Ta(
            min = item.taMin4,
            minLow = item.taMin4Low,
            minHigh = item.taMin4High,
            max = item.taMax4,
            maxLow = item.taMax4Low,
            maxHigh = item.taMax4High,
            n = 4
        )

        @Ignore
        val ta5 = Ta(
            min = item.taMin5,
            minLow = item.taMin5Low,
            minHigh = item.taMin5High,
            max = item.taMax5,
            maxLow = item.taMax5Low,
            maxHigh = item.taMax5High,
            n = 5
        )

        @Ignore
        val ta6 = Ta(
            min = item.taMin6,
            minLow = item.taMin6Low,
            minHigh = item.taMin6High,
            max = item.taMax6,
            maxLow = item.taMax6Low,
            maxHigh = item.taMax6High,
            n = 6
        )

        @Ignore
        val ta7 = Ta(
            min = item.taMin7,
            minLow = item.taMin7Low,
            minHigh = item.taMin7High,
            max = item.taMax7,
            maxLow = item.taMax7Low,
            maxHigh = item.taMax7High,
            n = 7
        )

        @Ignore
        val ta8 = Ta(
            min = item.taMin8,
            minLow = item.taMin8Low,
            minHigh = item.taMin8High,
            max = item.taMax8,
            maxLow = item.taMax8Low,
            maxHigh = item.taMax8High,
            n = 8
        )

        @Ignore
        val ta9 = Ta(
            min = item.taMin9,
            minLow = item.taMin9Low,
            minHigh = item.taMin9High,
            max = item.taMax9,
            maxLow = item.taMax9Low,
            maxHigh = item.taMax9High,
            n = 9
        )

        @Ignore
        val ta10 = Ta(
            min = item.taMin10,
            minLow = item.taMin10Low,
            minHigh = item.taMin10High,
            max = item.taMax10,
            maxLow = item.taMax10Low,
            maxHigh = item.taMax10High,
            n = 10
        )

        @Ignore
        val ta: ImmutableList<Ta> = persistentListOf(
            ta3, ta4, ta5, ta6, ta7, ta8, ta9, ta10
        )

        @Ignore
        val maxTa = ta.maxBy {
            it.max
        }

        @Ignore
        val minTa = ta.minBy {
            it.min
        }

        fun advancedDayBy(n: Int): ImmutableList<Ta> = if (n > Int.zero) {
            ta.map {
                it.copy(n = it.n.minus(n))
            }
                .toImmutableList()
        } else {
            ta
        }
    }

    @Serializable
    data class Remote(
        override val response: Response<Item>
    ) : MidTa, ResponseValidator<Item, Remote> {
        override val item: Item get() = response.items.first()

        override suspend fun validate(
            errorMsg: (Response<Item>) -> String,
            ifInvalid: (suspend (OpenAPIError) -> Remote)?
        ): Remote {
            return validate(this, errorMsg, ifInvalid)
        }

        fun toLocal(tmFc: String): Local {
            return Local(
                item = item,
                tmFc = tmFc
            )
        }
    }
}
