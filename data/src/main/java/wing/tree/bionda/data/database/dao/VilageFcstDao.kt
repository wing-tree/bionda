package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.constant.PATTERN_BASE_TIME
import wing.tree.bionda.data.extension.advanceHourOfDayBy
import wing.tree.bionda.data.model.VilageFcst.Local as VilageFcst

@Dao
interface VilageFcstDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vilageFcst: VilageFcst)

    @Query(
        """
            SELECT * FROM vilage_fcst 
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
    ): VilageFcst?

    @Query(
        """
            DELETE FROM vilage_fcst 
            WHERE baseDate < :baseDate 
            OR (baseDate = :baseDate AND baseTime <= :baseTime)
        """
    )
    suspend fun deleteUpTo(baseDate: String, baseTime: String)

    @Transaction
    suspend fun cacheInTransaction(vilageFcst: VilageFcst) {
        deleteUpTo(
            baseDate = vilageFcst.baseDate,
            baseTime = vilageFcst.baseTime.advanceHourOfDayBy(
                3, // TODO Replace with const.
                PATTERN_BASE_TIME
            )
        )

        insert(vilageFcst)
    }
}
