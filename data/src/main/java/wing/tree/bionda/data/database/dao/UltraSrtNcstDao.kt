package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.model.UltraSrtNcst.Local as UltraSrtNcst

@Dao
interface UltraSrtNcstDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ultraSrtNcst: UltraSrtNcst)

    @Query(
        """
            SELECT * FROM ultra_srt_ncst 
            WHERE baseDate = :baseDate 
            AND baseTime = :baseTime 
            AND nx = :nx 
            AND ny = :ny
            AND minute = :minute
        """
    )
    suspend fun load(
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int,
        minute: Int
    ): UltraSrtNcst?

    @Query("DELETE FROM ultra_srt_ncst")
    suspend fun clear()

    @Query(
        """
            DELETE FROM ultra_srt_ncst 
            WHERE baseDate < :baseDate 
            OR (baseDate = :baseDate AND baseTime < :baseTime)
        """
    )
    suspend fun deleteBefore(baseDate: String, baseTime: String)

    @Transaction
    suspend fun cacheInTransaction(ultraSrtNcst: UltraSrtNcst) {
        deleteBefore(
            baseDate = ultraSrtNcst.baseDate,
            baseTime = ultraSrtNcst.baseTime
        )

        insert(ultraSrtNcst)
    }
}
