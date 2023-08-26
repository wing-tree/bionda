package wing.tree.bionda.data.model.weather

import androidx.room.Entity
import androidx.room.Ignore
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.constant.COMMA
import wing.tree.bionda.data.constant.SPACE
import wing.tree.bionda.data.exception.OpenApiError
import wing.tree.bionda.data.exception.second
import wing.tree.bionda.data.exception.third
import wing.tree.bionda.data.validator.ResponseValidator

sealed interface LCRiseSetInfo {
    val item: Item

    @Serializable
    data class Item(
        val locdate: String,
        val location: String,
        val longitude: Int,
        val longitudeNum: Double,
        val latitude: Int,
        val latitudeNum: Double,
        val sunrise: String,
        val suntransit: String,
        val sunset: String,
        val moonrise: String,
        val moontransit: String,
        val moonset: String,
        val civilm: String,
        val civile: String,
        val nautm: String,
        val naute: String,
        val astm: String,
        val aste: String,
    )

    @Entity("lc_rise_set_info", primaryKeys = ["locdate", "longitude", "latitude"])
    data class Local(
        override val item: Item,
        val locdate: String,
        val longitude: Int,
        val latitude: Int,
    ) : LCRiseSetInfo {
        @Ignore
        val sunrise = item.sunrise

        @Ignore
        val sunset = item.sunset
    }

    data class Remote(
        override val response: Response<Item>
    ) : LCRiseSetInfo, ResponseValidator {
        override val item: Item = response.items.first()

        override fun validate(vararg params: String) {
            if (response.isUnsuccessful) {
                val header = response.header
                val errorCode = header.resultCode
                val errorMsg = buildList {
                    add("resultCode=${header.resultCode}")
                    add("resultMsg=${header.resultMsg}")
                    add("locdate=${params.first()}")
                    add("longitude=${params.second()}")
                    add("latitude=${params.third()}")
                }.joinToString("$COMMA$SPACE")

                throw OpenApiError(
                    errorCode = errorCode,
                    errorMsg = errorMsg
                )
            }
        }

        fun toLocal(): Local = with(item) {
            validate(locdate, "$longitude", "$latitude")

            return Local(
                item = this,
                locdate = locdate,
                longitude = longitude,
                latitude = latitude
            )
        }
    }
}
