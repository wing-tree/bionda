package wing.tree.bionda.data.model

import androidx.room.Entity
import androidx.room.Ignore
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.extension.zero

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
            val maxHigh: Int
        )

        @Ignore
        val ta3 = Ta(
            min = item.taMin3 ?: Int.zero,
            minLow = item.taMin3Low ?: Int.zero,
            minHigh = item.taMin3High ?: Int.zero,
            max = item.taMax3 ?: Int.zero,
            maxLow = item.taMax3Low ?: Int.zero,
            maxHigh = item.taMax3High ?: Int.zero
        )

        @Ignore
        val ta4 = Ta(
            min = item.taMin4,
            minLow = item.taMin4Low,
            minHigh = item.taMin4High,
            max = item.taMax4,
            maxLow = item.taMax4Low,
            maxHigh = item.taMax4High
        )

        @Ignore
        val ta5 = Ta(
            min = item.taMin5,
            minLow = item.taMin5Low,
            minHigh = item.taMin5High,
            max = item.taMax5,
            maxLow = item.taMax5Low,
            maxHigh = item.taMax5High
        )

        @Ignore
        val ta6 = Ta(
            min = item.taMin6,
            minLow = item.taMin6Low,
            minHigh = item.taMin6High,
            max = item.taMax6,
            maxLow = item.taMax6Low,
            maxHigh = item.taMax6High
        )

        @Ignore
        val ta7 = Ta(
            min = item.taMin7,
            minLow = item.taMin7Low,
            minHigh = item.taMin7High,
            max = item.taMax7,
            maxLow = item.taMax7Low,
            maxHigh = item.taMax7High
        )

        @Ignore
        val ta8 = Ta(
            min = item.taMin8,
            minLow = item.taMin8Low,
            minHigh = item.taMin8High,
            max = item.taMax8,
            maxLow = item.taMax8Low,
            maxHigh = item.taMax8High
        )

        @Ignore
        val ta9 = Ta(
            min = item.taMin9,
            minLow = item.taMin9Low,
            minHigh = item.taMin9High,
            max = item.taMax9,
            maxLow = item.taMax9Low,
            maxHigh = item.taMax9High
        )

        @Ignore
        val ta10 = Ta(
            min = item.taMin10,
            minLow = item.taMin10Low,
            minHigh = item.taMin10High,
            max = item.taMax10,
            maxLow = item.taMax10Low,
            maxHigh = item.taMax10High
        )

        @Ignore
        val items = persistentListOf(
            ta3, ta4, ta5, ta6,
            ta7, ta8, ta9, ta10
        )
    }

    @Serializable
    data class Remote(val response: Response<Item>) : MidTa {
        override val item: Item = response.body.items.item.first()

        fun toLocal(tmFc: String, item: Item? = null) = Local(
            item = item ?: this.item,
            tmFc = tmFc
        )
    }
}
