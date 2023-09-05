package wing.tree.bionda.data.model

import androidx.room.Entity
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.core.Response
import wing.tree.bionda.data.exception.OpenAPIError
import wing.tree.bionda.data.validator.ResponseValidator

interface LivingWthrIdx {
    val item: Item

    data class H(val n: Int, val h: String)

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
    ) : List<H> by listOf(
        H(0, h0), H(3, h3), H(6, h6),
        H(9, h9), H(12, h12), H(15, h15),
        H(18, h18), H(21, h21), H(24, h24),
        H(27, h27), H(30, h30), H(33, h33),
        H(36, h36), H(39, h39), H(42, h42),
        H(45, h45), H(48, h48), H(51, h51),
        H(54, h54), H(57, h57), H(60, h60),
        H(63, h63), H(66, h66), H(69, h69),
        H(72, h72), H(75, h75)
    )

    sealed interface AirDiffusionIdx : LivingWthrIdx {
        @Entity(tableName = "air_diffusion_idx", primaryKeys = ["areaNo", "time"])
        data class Local(
            override val item: Item,
            val areaNo: String,
            val time: String
        ) : AirDiffusionIdx

        @Serializable
        data class Remote(
            override val response: Response<Item>
        ) : AirDiffusionIdx, ResponseValidator<Item, Remote> {
            override val item: Item get() = response.items.first()

            override suspend fun validate(
                errorMsg: (Response<Item>) -> String,
                ifInvalid: (suspend (OpenAPIError) -> Remote)?
            ): Remote {
                return validate(this, errorMsg, ifInvalid)
            }

            fun toLocal(
                areaNo: String,
                time: String
            ): Local = Local(
                item = item,
                areaNo = areaNo,
                time = time
            )
        }
    }

    sealed interface UVIdx : LivingWthrIdx {
        @Entity(tableName = "uv_idx", primaryKeys = ["areaNo", "time"])
        data class Local(
            override val item: Item,
            val areaNo: String,
            val time: String
        ) : UVIdx

        @Serializable
        data class Remote(
            override val response: Response<Item>
        ) : UVIdx, ResponseValidator<Item, Remote> {
            override val item: Item get() = response.items.first()

            override suspend fun validate(
                errorMsg: (Response<Item>) -> String,
                ifInvalid: (suspend (OpenAPIError) -> Remote)?
            ): Remote {
                return validate(this, errorMsg, ifInvalid)
            }

            fun toLocal(
                areaNo: String,
                time: String
            ): Local = Local(
                item = item,
                areaNo = areaNo,
                time = time
            )
        }
    }
}
