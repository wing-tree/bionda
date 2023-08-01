package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import wing.tree.bionda.data.model.Notice

@Dao
interface NoticeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notice: Notice): Long

    @Update
    suspend fun update(notice: Notice)

    @Delete
    suspend fun delete(notice: Notice)

    @Query("SELECT * FROM notice WHERE notificationId = :notificationId")
    suspend fun get(notificationId: Long): Notice?

    @Query("SELECT * FROM notice ORDER BY hour AND minute ASC")
    fun load(): Flow<List<Notice>>
}
