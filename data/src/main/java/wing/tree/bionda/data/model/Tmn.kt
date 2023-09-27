package wing.tree.bionda.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tmn")
data class Tmn(
    @PrimaryKey
    val baseData: String,
    val value: String
)
