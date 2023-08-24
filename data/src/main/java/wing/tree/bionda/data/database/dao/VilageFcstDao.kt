package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.model.weather.VilageFcst.Local as VilageFcst

@Dao
interface VilageFcstDao {
    @Insert
    suspend fun insert(vilageFcst: VilageFcst)

    @Query(
        """
            SELECT * FROM vilage_fcst WHERE baseDate = :baseDate 
            AND baseTime = :baseTime 
            AND nx = :nx 
            AND ny = :ny
        """
    )
    suspend fun load(
        baseDate : String,
        baseTime : String,
        nx: Int,
        ny: Int
    ): VilageFcst?

    @Query("DELETE FROM vilage_fcst")
    suspend fun clear()

    @Transaction
    suspend fun clearAndInsert(vilageFcst: VilageFcst) {
        clear()
        insert(vilageFcst)
    }
}
