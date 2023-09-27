package wing.tree.bionda.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tmx")
data class Tmx(
    @PrimaryKey
    @ColumnInfo(name = "base_date")
    val baseDate: String,
    val value: String
)
