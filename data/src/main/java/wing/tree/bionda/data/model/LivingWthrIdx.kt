package wing.tree.bionda.data.model

import androidx.room.Entity
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.core.Response
import wing.tree.bionda.data.exception.OpenAPIError
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.validator.ResponseValidator

interface LivingWthrIdx {
    val item: Item

    data class H(val n: Int, val h: String)

    @Serializable
    data class Item(
        val code: String,
        val areaNo: String,
        val date: String,
        val h0: String = String.empty,
        val h1: String = String.empty,
        val h2: String = String.empty,
        val h3: String = String.empty,
        val h4: String = String.empty,
        val h5: String = String.empty,
        val h6: String = String.empty,
        val h7: String = String.empty,
        val h8: String = String.empty,
        val h9: String = String.empty,
        val h10: String = String.empty,
        val h11: String = String.empty,
        val h12: String = String.empty,
        val h13: String = String.empty,
        val h14: String = String.empty,
        val h15: String = String.empty,
        val h16: String = String.empty,
        val h17: String = String.empty,
        val h18: String = String.empty,
        val h19: String = String.empty,
        val h20: String = String.empty,
        val h21: String = String.empty,
        val h22: String = String.empty,
        val h23: String = String.empty,
        val h24: String = String.empty,
        val h25: String = String.empty,
        val h26: String = String.empty,
        val h27: String = String.empty,
        val h28: String = String.empty,
        val h29: String = String.empty,
        val h30: String = String.empty,
        val h31: String = String.empty,
        val h32: String = String.empty,
        val h33: String = String.empty,
        val h34: String = String.empty,
        val h35: String = String.empty,
        val h36: String = String.empty,
        val h37: String = String.empty,
        val h38: String = String.empty,
        val h39: String = String.empty,
        val h40: String = String.empty,
        val h41: String = String.empty,
        val h42: String = String.empty,
        val h43: String = String.empty,
        val h44: String = String.empty,
        val h45: String = String.empty,
        val h46: String = String.empty,
        val h47: String = String.empty,
        val h48: String = String.empty,
        val h49: String = String.empty,
        val h50: String = String.empty,
        val h51: String = String.empty,
        val h52: String = String.empty,
        val h53: String = String.empty,
        val h54: String = String.empty,
        val h55: String = String.empty,
        val h56: String = String.empty,
        val h57: String = String.empty,
        val h58: String = String.empty,
        val h59: String = String.empty,
        val h60: String = String.empty,
        val h61: String = String.empty,
        val h62: String = String.empty,
        val h63: String = String.empty,
        val h64: String = String.empty,
        val h65: String = String.empty,
        val h66: String = String.empty,
        val h67: String = String.empty,
        val h68: String = String.empty,
        val h69: String = String.empty,
        val h70: String = String.empty,
        val h71: String = String.empty,
        val h72: String = String.empty,
        val h73: String = String.empty,
        val h74: String = String.empty,
        val h75: String = String.empty,
        val h76: String = String.empty,
        val h77: String = String.empty,
        val h78: String = String.empty
    )

    sealed interface AirDiffusionIdx : LivingWthrIdx {
        val items: PersistentList<H> get() = with(item) {
            persistentListOf(
                H(0, h0), H(3, h3), H(6, h6),
                H(9, h9), H(12, h12), H(15, h15),
                H(18, h18), H(21, h21), H(24, h24),
                H(27, h27), H(30, h30), H(33, h33),
                H(36, h36), H(39, h39), H(42, h42),
                H(45, h45), H(48, h48), H(51, h51),
                H(54, h54), H(57, h57), H(60, h60),
                H(63, h63), H(66, h66), H(69, h69),
                H(72, h72), H(75, h75), H(78, h78)
            )
        }

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

        object Level {
            const val LOW = "100"
            const val NORMAL = "75"
            const val HIGH = "50"
            const val VERY_HIGH = "25"
        }
    }

    sealed interface UVIdx : LivingWthrIdx {
        val items: PersistentList<H> get() = with(item) {
            persistentListOf(
                H(0, h0), H(3, h3), H(6, h6),
                H(9, h9), H(12, h12), H(15, h15),
                H(18, h18), H(21, h21), H(24, h24),
                H(27, h27), H(30, h30), H(33, h33),
                H(36, h36), H(39, h39), H(42, h42),
                H(45, h45), H(48, h48), H(51, h51),
                H(54, h54), H(57, h57), H(60, h60),
                H(63, h63), H(66, h66), H(69, h69),
                H(72, h72), H(75, h75), H(78, h78)
            )
        }

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
