package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import wing.tree.bionda.data.model.Tmn

@Dao
interface TmnDao {
    @Insert
    suspend fun insert(tmn: Tmn)

    @Query("SELECT * FROM tmn WHERE base_date = :baseDate")
    suspend fun get(baseDate: String): Tmn?
}
