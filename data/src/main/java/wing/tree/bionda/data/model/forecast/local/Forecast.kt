package wing.tree.bionda.data.model.forecast.local

import androidx.room.Entity
import kotlinx.collections.immutable.ImmutableList
import wing.tree.bionda.data.model.forecast.Forecast
import wing.tree.bionda.data.model.forecast.Item

@Entity(
    tableName = "forecast",
    primaryKeys = [
        "nx",
        "ny",
        "baseDate",
        "baseTime"
    ]
)
data class Forecast(
    override val items: ImmutableList<Item>,
    override val nx: Int,
    override val ny: Int,
    val baseDate: String,
    val baseTime: String,
) : Forecast
