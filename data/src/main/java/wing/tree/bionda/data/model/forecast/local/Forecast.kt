package wing.tree.bionda.data.model.forecast.local

import androidx.room.Entity
import kotlinx.collections.immutable.ImmutableList
import wing.tree.bionda.data.model.forecast.Forecast
import wing.tree.bionda.data.model.forecast.Item

@Entity(tableName = "forecast", primaryKeys = ["apiDeliveryDate", "apiDeliveryTime"])
data class Forecast(
    override val items: ImmutableList<Item>,
    override val nx: Int,
    override val ny: Int,
    val apiDeliveryDate: String,
    val apiDeliveryTime: String,
) : Forecast
