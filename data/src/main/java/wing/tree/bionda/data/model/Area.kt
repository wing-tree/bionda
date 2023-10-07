package wing.tree.bionda.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import wing.tree.bionda.data.constant.SPACE
import wing.tree.bionda.data.core.Address
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
    val favorited: Boolean
) : Parcelable {
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

    fun toAddress() = Address(
        adminArea = level1,
        locality = level2,
        thoroughfare = level3
    )
}
