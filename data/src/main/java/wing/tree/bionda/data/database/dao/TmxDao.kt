package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import wing.tree.bionda.data.model.Tmx

@Dao
interface TmxDao {
    @Insert
    suspend fun insert(tmx: Tmx)

    @Query("SELECT * FROM tmx")
    fun load(): Flow<Tmx>
}
