package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.model.MidTa.Local as MidTa

@Dao
interface MidTaDao {
    @Insert
    suspend fun insert(midTa: MidTa)

    @Query("SELECT * FROM mid_ta WHERE regId = :regId AND tmFc = :tmFc")
    suspend fun load(regId: String, tmFc: String): MidTa?

    @Query("DELETE FROM mid_ta")
    suspend fun clear()

    @Transaction
    suspend fun clearAndInsert(midTa: MidTa) {
        clear()
        insert(midTa)
    }
}
