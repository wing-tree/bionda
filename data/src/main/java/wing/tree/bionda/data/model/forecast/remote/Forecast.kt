package wing.tree.bionda.data.model.forecast.remote

import kotlinx.serialization.Serializable
import wing.tree.bionda.data.model.forecast.Forecast
import wing.tree.bionda.data.model.forecast.Item

@Serializable
data class Forecast(
    val response: Response
) : Forecast {
    override val items: List<Item> get() = response.body.items.item

    @Serializable
    data class Response(
        val header: Header,
        val body: Body
    )

    @Serializable
    data class Header(
        val resultCode: Int,
        val resultMsg: String
    )

    @Serializable
    data class Body(
        val dataType: String,
        val items: Items,
        val numOfRows: Int,
        val pageNo: Int,
        val totalCount: Int
    )

    @Serializable
    data class Items(
        val item: List<Item>
    )
}
