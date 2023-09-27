package wing.tree.bionda.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tmx")
data class Tmx(
    @PrimaryKey
    val baseData: String,
    val value: String
)
