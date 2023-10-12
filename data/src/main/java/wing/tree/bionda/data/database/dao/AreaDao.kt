package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import wing.tree.bionda.data.model.Area

@Dao
interface AreaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(area: Area)

    @Update
    suspend fun update(area: Area)

    @Suppress("unused")
    @Delete
    suspend fun delete(area: Area)

    @Query("SELECT * FROM area")
    suspend fun load(): List<Area>
}
