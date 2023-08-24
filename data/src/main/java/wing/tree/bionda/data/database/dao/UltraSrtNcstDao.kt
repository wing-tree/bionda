package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.model.weather.UltraSrtNcst

@Dao
interface UltraSrtNcstDao {
    @Insert
    suspend fun insert(vilageFcst: UltraSrtNcst.Local)

    @Query(
        """
            SELECT * FROM ultra_srt_ncst WHERE baseDate = :baseDate 
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
    ): UltraSrtNcst.Local?

    @Query("DELETE FROM ultra_srt_ncst")
    suspend fun clear()

    @Transaction
    suspend fun clearAndInsert(vilageFcst: UltraSrtNcst.Local) {
        clear()
        insert(vilageFcst)
    }
}
