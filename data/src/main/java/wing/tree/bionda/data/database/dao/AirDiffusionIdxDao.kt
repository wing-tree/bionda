package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.model.LivingWthrIdx.AirDiffusionIdx.Local as AirDiffusionIdx

@Dao
interface AirDiffusionIdxDao {
    @Insert
    suspend fun insert(airDiffusionIdx: AirDiffusionIdx)

    @Query(
        """
            SELECT * FROM air_diffusion_idx 
            WHERE areaNo = :areaNo 
            AND time = :time
        """
    )
    suspend fun load(
        areaNo: String,
        time: String
    ): AirDiffusionIdx?

    @Query("DELETE FROM air_diffusion_idx")
    suspend fun clear()

    @Transaction
    suspend fun clearAndInsert(airDiffusionIdx: AirDiffusionIdx) {
        clear()
        insert(airDiffusionIdx)
    }
}