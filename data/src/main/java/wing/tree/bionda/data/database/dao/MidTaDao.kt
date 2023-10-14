package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.model.MidTa.Local as MidTa

@Dao
interface MidTaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(midTa: MidTa)

    @Query("DELETE FROM mid_ta WHERE tmFc < :tmFc")
    suspend fun deleteBefore(tmFc: String)

    @Query("SELECT * FROM mid_ta WHERE regId = :regId AND tmFc = :tmFc")
    suspend fun load(regId: String, tmFc: String): MidTa?

    @Transaction
    suspend fun cacheInTransaction(midTa: MidTa) {
        deleteBefore(midTa.tmFc)
        insert(midTa)
    }
}
