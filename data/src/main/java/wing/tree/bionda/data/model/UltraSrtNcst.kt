package wing.tree.bionda.data.model

import androidx.room.Entity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.core.Response
import wing.tree.bionda.data.exception.OpenAPIError
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.service.VilageFcstInfoService
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
        tableName = "ultra_srt_ncst",
        primaryKeys = [
            "nx",
            "ny",
            "baseDate",
            "baseTime",
            "minute"
        ]
    )
    data class Local(
        override val items: ImmutableList<Item>,
        override val nx: Int,
        override val ny: Int,
        val baseDate: String,
        val baseTime: String,
        val minute: Int
    ) : UltraSrtNcst

    @Serializable
    data class Remote(
        override val response: Response<Item>
    ) : UltraSrtNcst, ResponseValidator<Item, Remote> {
        override val items: List<Item> get() = response.items
        override val nx: Int get() = items.firstOrNull()?.nx ?: Int.zero
        override val ny: Int get() = items.firstOrNull()?.ny ?: Int.zero

        override suspend fun validate(
            errorMsg: (Response<Item>) -> String,
            ifInvalid: (suspend (OpenAPIError) -> Remote)?
        ): Remote {
            return validate(this, errorMsg, null)
        }

        fun toLocal(
            params: VilageFcstInfoService.Params,
            minute: Int
        ): Local = with(params) {
            Local(
                items = items.toImmutableList(),
                baseDate = baseDate,
                baseTime = baseTime,
                nx = this.nx,
                ny = this.ny,
                minute = minute
            )
        }
    }
}
