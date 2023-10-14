package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.constant.PATTERN_BASE_TIME
import wing.tree.bionda.data.extension.advanceHourOfDayBy
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
        """
    )
    suspend fun load(
        baseDate : String,
        baseTime : String,
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
        baseDate : String,
        baseTime : String,
        nx: Int,
        ny: Int,
        minute: Int,
    ): UltraSrtFcst?

    @Query(
        """
            DELETE FROM ultra_srt_fcst 
            WHERE baseDate < :baseDate 
            OR (baseDate = :baseDate AND baseTime <= :baseTime)
        """
    )
    suspend fun deleteUpTo(baseDate: String, baseTime: String)

    @Transaction
    suspend fun cacheInTransaction(ultraSrtFcst: UltraSrtFcst) {
        deleteUpTo(
            ultraSrtFcst.baseDate,
            ultraSrtFcst.baseTime.advanceHourOfDayBy(
                1,
                PATTERN_BASE_TIME
            )
        )

        insert(ultraSrtFcst)
    }
}
