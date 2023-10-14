package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.constant.PATTERN_TM_FC
import wing.tree.bionda.data.extension.advanceHourOfDayBy
import java.time.LocalTime
import wing.tree.bionda.data.model.MidTa.Local as MidTa

@Dao
interface MidTaDao {
    @Insert
    suspend fun insert(midTa: MidTa)

    @Query("SELECT * FROM mid_ta WHERE regId = :regId AND tmFc = :tmFc")
    suspend fun load(regId: String, tmFc: String): MidTa?

    @Query("DELETE FROM mid_ta WHERE tmFc <= :tmFc")
    suspend fun deleteUpTo(tmFc: String)

    @Transaction
    suspend fun cacheInTransaction(midTa: MidTa) {
        deleteUpTo(
            midTa.tmFc.advanceHourOfDayBy(
                LocalTime.NOON.hour,
                PATTERN_TM_FC
            )
        )

        insert(midTa)
    }
}
