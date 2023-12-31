package wing.tree.bionda.data.model

import android.os.Parcelable
import androidx.compose.runtime.mutableStateOf
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import wing.tree.bionda.data.constant.SPACE
import wing.tree.bionda.data.extension.isNotNanOrBlank

@Entity(tableName = "area")
@Parcelize
data class Area(
    @PrimaryKey(autoGenerate = false)
    val no: String,
    val level1: String,
    val level2: String,
    val level3: String,
    val nx: Int,
    val ny: Int,
    val longitude: Double,
    val latitude: Double,
) : Parcelable {
    @Ignore
    @IgnoredOnParcel
    val favorited = mutableStateOf(false)

    @Ignore
    @IgnoredOnParcel
    val name: String = buildString {
        append(level1)

        if (level2.isNotNanOrBlank()) {
            append(SPACE)
            append(level2)
        }

        if (level3.isNotNanOrBlank()) {
            append(SPACE)
            append(level3)
        }
    }
}
