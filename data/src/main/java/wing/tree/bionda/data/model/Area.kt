package wing.tree.bionda.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "area")
data class Area(
    @PrimaryKey(autoGenerate = false)
    val no: String,
    val nx: Int,
    val ny: Int,
    val longitude: Double,
    val latitude: Double
)
