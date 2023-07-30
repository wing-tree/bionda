package wing.tree.bionda.data.model.forecast.local

import androidx.room.Entity
import wing.tree.bionda.data.model.forecast.Forecast
import wing.tree.bionda.data.model.forecast.Item

@Entity(tableName = "forecast", primaryKeys = ["requestDate", "requestTime"])
data class Forecast(
    override val items: List<Item>,
    val requestDate: String,
    val requestTime: String
) : Forecast
