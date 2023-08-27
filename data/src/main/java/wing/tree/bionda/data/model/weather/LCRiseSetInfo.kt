package wing.tree.bionda.data.model.weather

import androidx.room.Entity
import androidx.room.Ignore
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.constant.COMMA
import wing.tree.bionda.data.constant.SPACE
import wing.tree.bionda.data.exception.OpenApiError
import wing.tree.bionda.data.exception.second
import wing.tree.bionda.data.exception.third
import wing.tree.bionda.data.extension.not

sealed interface LCRiseSetInfo {
    val item: Item

    @Serializable
    @Xml
    data class Item(
        @PropertyElement
        val locdate: String,
        @PropertyElement
        val location: String,
        @PropertyElement
        val longitude: String,
        @PropertyElement
        val longitudeNum: String,
        @PropertyElement
        val latitude: String,
        @PropertyElement
        val latitudeNum: String,
        @PropertyElement
        val sunrise: String,
        @PropertyElement
        val suntransit: String,
        @PropertyElement
        val sunset: String,
        @PropertyElement
        val moonrise: String,
        @PropertyElement
        val moontransit: String,
        @PropertyElement
        val moonset: String,
        @PropertyElement
        val civilm: String,
        @PropertyElement
        val civile: String,
        @PropertyElement
        val nautm: String,
        @PropertyElement
        val naute: String,
        @PropertyElement
        val astm: String,
        @PropertyElement
        val aste: String
    )

    @Entity("lc_rise_set_info", primaryKeys = ["locdate", "longitude", "latitude"])
    data class Local(
        override val item: Item,
        val locdate: String,
        val longitude: String,
        val latitude: String,
    ) : LCRiseSetInfo {
        @Ignore
        val sunrise = item.sunrise

        @Ignore
        val sunset = item.sunset
    }

    data class Remote(
        val response: Response
    ) : LCRiseSetInfo {
        override val item: Item = response.items.first()

        private fun validate(vararg params: String) {
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
            validate(locdate, longitude, latitude)

            return Local(
                item = this,
                locdate = locdate,
                longitude = longitude,
                latitude = latitude
            )
        }
    }

    @Xml
    data class Response(
        @Element
        val header: Header,
        @Element
        val body: Body
    ) {
        val isUnsuccessful: Boolean get() = header.resultCode not OpenApiError.ERROR_CODE_00
        val items: Items get() = body.items
    }

    @Xml
    data class Header(
        @PropertyElement
        val resultCode: String,
        @PropertyElement
        val resultMsg: String
    )

    @Xml
    data class Body(
        @Element
        val items: Items,
        @PropertyElement
        val numOfRows: Int,
        @PropertyElement
        val pageNo: Int,
        @PropertyElement
        val totalCount: Int
    )

    @Xml
    data class Items(
        @Element
        val item: List<Item> = emptyList()
    ) : List<Item> by item
}
