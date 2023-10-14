package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import wing.tree.bionda.data.service.RiseSetInfoService
import wing.tree.bionda.data.model.LCRiseSetInfo.Local as LCRiseSetInfo

@Dao
interface LCRiseSetInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lcRiseSetInfo: LCRiseSetInfo)

    @Query(
        """
            SELECT * FROM lc_rise_set_info 
            WHERE locdate = :locdate
            AND longitude = :longitude 
            AND latitude = :latitude
        """
    )
    suspend fun load(
        locdate: String,
        longitude: String,
        latitude: String
    ): LCRiseSetInfo?

    @Query(
        """
            DELETE FROM lc_rise_set_info 
            WHERE locdate = :locdate
            AND longitude = :longitude 
            AND latitude = :latitude
        """
    )
    suspend fun delete(locdate: String, longitude: String, latitude: String)

    @Transaction
    suspend fun deleteAndInsert(
        params: RiseSetInfoService.Params,
        lcRiseSetInfo: LCRiseSetInfo
    ) {
        delete(
            locdate = params.locdate,
            longitude = params.longitude,
            latitude = params.latitude,
        )

        insert(lcRiseSetInfo)
    }
}
