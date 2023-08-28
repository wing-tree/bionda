package wing.tree.bionda.data.model

import androidx.room.Entity
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.constant.COMMA
import wing.tree.bionda.data.constant.SPACE
import wing.tree.bionda.data.exception.OpenApiError
import wing.tree.bionda.data.exception.second
import wing.tree.bionda.data.model.core.Response
import wing.tree.bionda.data.validator.ResponseValidator

sealed interface UVIdx {
    val item: Item

    @Serializable
    data class Item(
        val code: String,
        val areaNo: String,
        val date: String,
        val h0: String,
        val h3: String,
        val h6: String,
        val h9: String,
        val h12: String,
        val h15: String,
        val h18: String,
        val h21: String,
        val h24: String,
        val h27: String,
        val h30: String,
        val h33: String,
        val h36: String,
        val h39: String,
        val h42: String,
        val h45: String,
        val h48: String,
        val h51: String,
        val h54: String,
        val h57: String,
        val h60: String,
        val h63: String,
        val h66: String,
        val h69: String,
        val h72: String,
        val h75: String
    ) : List<String> by listOf(
        h0, h3, h6, h9, h12, h15, h18, h21,
        h24, h27, h30, h33, h36, h39, h42, h45,
        h48, h51, h54, h57, h60, h63, h66, h69,
        h72, h75
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
            if (response.isUnsuccessful) {
                val header = response.header
                val errorCode = header.resultCode
                val errorMsg = buildList {
                    add("resultCode=${header.resultCode}")
                    add("resultMsg=${header.resultMsg}")
                    add("areaNo=${params.first()}")
                    add("time=${params.second()}")
                }
                    .joinToString("$COMMA$SPACE")

                throw OpenApiError(
                    errorCode = errorCode,
                    errorMsg = errorMsg
                )
            }
        }

        fun toLocal(
            areaNo: String,
            time: String
        ): Local {
            validate(areaNo, time)

            return Local(
                item = item,
                areaNo = areaNo,
                time = time
            )
        }
    }
}