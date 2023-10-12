package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import wing.tree.bionda.data.model.Tmx

@Dao
interface TmxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tmx: Tmx)

    @Query("DELETE FROM tmx WHERE base_date < :baseDate")
    suspend fun deleteBefore(baseDate: String)

    @Query("SELECT * FROM tmx WHERE base_date = :baseDate")
    suspend fun get(baseDate: String): Tmx?
}
