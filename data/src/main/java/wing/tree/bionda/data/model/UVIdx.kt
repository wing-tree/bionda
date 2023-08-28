package wing.tree.bionda.data.model

import androidx.room.Entity
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.model.core.Response
import wing.tree.bionda.data.validator.ResponseValidator

sealed interface UVIdx {
    val item: Item

    @Serializable
    data class Item(
        val code: String,
        val areaNo: String,
        val date: String,
        val h0: Int,
        val h3: Int,
        val h6: Int,
        val h9: Int,
        val h12: Int,
        val h15: Int,
        val h18: Int,
        val h21: Int,
        val h24: Int,
        val h27: Int,
        val h30: Int,
        val h33: Int,
        val h36: Int,
        val h39: Int,
        val h42: Int,
        val h45: Int,
        val h48: Int,
        val h51: Int,
        val h54: Int,
        val h57: Int,
        val h60: Int,
        val h63: Int,
        val h66: Int,
        val h69: Int,
        val h72: Int,
        val h75: Int
    )

    @Entity(tableName = "uv_idx", primaryKeys = ["areaNo", "time"])
    data class Local(
        override val item: Item,
        val areaNo: String,
        val time: String
    ) : UVIdx

    @Serializable
    data class Remote(
        override val response: Response<Item>
    ) : UVIdx, ResponseValidator {
        override val item: Item get() = response.items.first()

        override fun validate(vararg params: String) {
            TODO("Not yet implemented")
        }
    }
}