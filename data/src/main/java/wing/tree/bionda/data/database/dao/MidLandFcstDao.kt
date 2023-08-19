package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.model.MidLandFcst.Local as MidLandFcst

@Dao
interface MidLandFcstDao {
    @Insert
    suspend fun insert(midLandFcst: MidLandFcst)

    @Query("SELECT * FROM mid_land_fcst WHERE regId = :regId AND tmFc = :tmFc")
    suspend fun load(regId: String, tmFc: String): MidLandFcst?

    @Query("DELETE FROM mid_land_fcst")
    suspend fun clear()

    @Transaction
    suspend fun clearAndInsert(midLandFcst: MidLandFcst) {
        clear()
        insert(midLandFcst)
    }
}
