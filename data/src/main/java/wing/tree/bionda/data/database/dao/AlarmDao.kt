package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import wing.tree.bionda.data.model.Alarm

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(alarm: Alarm): Long

    @Update
    suspend fun update(alarm: Alarm)

    @Update
    suspend fun updateAll(alarms: List<Alarm>)

    @Delete
    suspend fun delete(alarm: Alarm)

    @Delete
    suspend fun deleteAll(alarms: List<Alarm>)

    @Query("SELECT * FROM alarm WHERE id = :id")
    suspend fun get(id: Long): Alarm?

    @Query("SELECT EXISTS(SELECT * FROM alarm WHERE hour = :hour AND minute = :minute)")
    suspend fun isExists(hour: Int, minute: Int): Boolean

    @Query("SELECT * FROM alarm ORDER BY hour AND minute ASC")
    fun load(): Flow<List<Alarm>>
}
