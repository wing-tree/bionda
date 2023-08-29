package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.model.UltraSrtFcst.Local as UltraSrtFcst

@Dao
interface UltraSrtFcstDao {
    @Insert
    suspend fun insert(ultraSrtFcst: UltraSrtFcst)

    @Query(
        """
            SELECT * FROM ultra_srt_fcst 
            WHERE baseDate = :baseDate 
            AND baseTime = :baseTime 
            AND nx = :nx 
            AND ny = :ny
            AND minute = :minute
        """
    )
    suspend fun load(
        baseDate : String,
        baseTime : String,
        nx: Int,
        ny: Int,
        minute: Int,
    ): UltraSrtFcst?

    @Query("DELETE FROM ultra_srt_fcst")
    suspend fun clear()

    @Transaction
    suspend fun clearAndInsert(ultraSrtFcst: UltraSrtFcst) {
        clear()
        insert(ultraSrtFcst)
    }
}
