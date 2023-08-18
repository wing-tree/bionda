package wing.tree.bionda.data.model.forecast.remote

import kotlinx.serialization.Serializable
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.model.Response
import wing.tree.bionda.data.model.forecast.Forecast
import wing.tree.bionda.data.model.forecast.Item

@Serializable
data class Forecast(
    val response: Response<Item>
) : Forecast {
    override val items: List<Item> get() = response.body.items.item
    override val nx: Int = items.firstOrNull()?.nx ?: Int.zero
    override val ny: Int = items.firstOrNull()?.ny ?: Int.zero
}
