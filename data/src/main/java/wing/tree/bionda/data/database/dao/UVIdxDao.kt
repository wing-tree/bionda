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

    @Query("DELETE FROM uv_idx WHERE time < :time")
    suspend fun deleteBefore(time: String)

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

    @Transaction
    suspend fun cacheInTransaction(uvIdx: UVIdx) {
        deleteBefore(uvIdx.time)
        insert(uvIdx)
    }
}
