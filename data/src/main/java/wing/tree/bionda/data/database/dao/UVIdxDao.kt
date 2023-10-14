package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.model.LivingWthrIdx.UVIdx.Local as UVIdx

@Dao
interface UVIdxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(uvIdx: UVIdx)

    @Query(
        """
            SELECT * FROM uv_idx 
            WHERE areaNo = :areaNo 
            AND time = :time
        """
    )
    suspend fun load(
        areaNo: String,
        time: String
    ): UVIdx?

    @Query("DELETE FROM uv_idx")
    suspend fun clear()

    @Transaction
    suspend fun clearAndInsert(uvIdx: UVIdx) {
        clear()
        insert(uvIdx)
    }
}
