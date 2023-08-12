package wing.tree.bionda.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.zero

@Entity(tableName = "notice")
@Parcelize
data class Notice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = Long.zero,
    val hour: Int,
    val minute: Int,
    val on: Boolean = true
) : Parcelable {
    @IgnoredOnParcel
    @get:Ignore
    val notificationId: Int get() = id.inc().int

    @IgnoredOnParcel
    @get:Ignore
    val off: Boolean get() = on.not()

    @IgnoredOnParcel
    @get:Ignore
    val requestCode: Int get() = notificationId
}
