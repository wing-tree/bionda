package wing.tree.bionda.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.serialization.Serializable
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.zero

@Entity(tableName = "notice")
data class Notice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = Long.zero,
    val hour: Int,
    val minute: Int,
    val on: Boolean = true,
    val types: ImmutableList<Type> = persistentListOf(Type.RAIN, Type.SNOW)
) {
    @Serializable
    enum class Type {
        RAIN, SNOW
    }

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
