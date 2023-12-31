package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.model.UltraSrtFcst.Local as UltraSrtFcst

@Dao
interface UltraSrtFcstDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ultraSrtFcst: UltraSrtFcst)

    @Query(
        """
            DELETE FROM ultra_srt_fcst 
            WHERE baseDate < :baseDate 
            OR (baseDate = :baseDate AND baseTime < :baseTime)
        """
    )
    suspend fun deleteBefore(baseDate: String, baseTime: String)

    @Query(
        """
            SELECT * FROM ultra_srt_fcst 
            WHERE baseDate = :baseDate 
            AND baseTime = :baseTime 
            AND nx = :nx 
            AND ny = :ny
        """
    )
    suspend fun load(
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int
    ): UltraSrtFcst?

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
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int,
        minute: Int,
    ): UltraSrtFcst?

    @Transaction
    suspend fun cacheInTransaction(ultraSrtFcst: UltraSrtFcst) {
        deleteBefore(
            baseDate = ultraSrtFcst.baseDate,
            baseTime = ultraSrtFcst.baseTime
        )

        insert(ultraSrtFcst)
    }
}
