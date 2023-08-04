package wing.tree.bionda.data.model.forecast.local

import androidx.room.Entity
import wing.tree.bionda.data.model.forecast.Forecast
import wing.tree.bionda.data.model.forecast.Item

@Entity(tableName = "forecast", primaryKeys = ["apiDeliveryDate", "apiDeliveryTime"])
data class Forecast(
    override val items: List<Item>,
    override val nx: Int,
    override val ny: Int,
    val apiDeliveryDate: String,
    val apiDeliveryTime: String,
) : Forecast
