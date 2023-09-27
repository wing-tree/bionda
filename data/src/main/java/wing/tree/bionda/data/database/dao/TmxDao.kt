package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import wing.tree.bionda.data.model.Tmx

@Dao
interface TmxDao {
    @Insert
    suspend fun insert(tmx: Tmx)

    @Query("SELECT * FROM tmx WHERE base_date = :baseDate")
    suspend fun get(baseDate: String): Tmx?
}
