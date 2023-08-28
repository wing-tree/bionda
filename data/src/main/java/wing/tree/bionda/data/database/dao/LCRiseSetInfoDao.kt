package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.model.LCRiseSetInfo.Local as LCRiseSetInfo

@Dao
interface LCRiseSetInfoDao {
    @Insert
    suspend fun insert(lcRiseSetInfo: LCRiseSetInfo)

    @Query(
        """
            SELECT * FROM lc_rise_set_info 
            WHERE locdate = :locdate
            AND (primaryLongitude = :longitude AND primaryLatitude = :latitude)
            OR (secondaryLongitude = :longitude AND secondaryLatitude = :latitude);
        """
    )
    suspend fun load(
        locdate: String,
        longitude: String,
        latitude: String
    ): LCRiseSetInfo?

    @Query("DELETE FROM lc_rise_set_info")
    suspend fun clear()

    @Transaction
    suspend fun clearAndInsert(lcRiseSetInfo: LCRiseSetInfo) {
        clear()
        insert(lcRiseSetInfo)
    }
}
