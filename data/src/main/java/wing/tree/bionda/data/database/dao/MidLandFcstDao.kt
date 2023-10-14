package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.model.MidLandFcst.Local as MidLandFcst

@Dao
interface MidLandFcstDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(midLandFcst: MidLandFcst)

    @Query("DELETE FROM mid_land_fcst WHERE tmFc < :tmFc")
    suspend fun deleteBefore(tmFc: String)

    @Query("SELECT * FROM mid_land_fcst WHERE regId = :regId AND tmFc = :tmFc")
    suspend fun load(regId: String, tmFc: String): MidLandFcst?

    @Transaction
    suspend fun cacheInTransaction(midLandFcst: MidLandFcst) {
        deleteBefore(midLandFcst.tmFc)
        insert(midLandFcst)
    }
}
