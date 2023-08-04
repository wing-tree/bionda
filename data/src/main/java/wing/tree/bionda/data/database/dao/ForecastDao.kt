package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import wing.tree.bionda.data.model.forecast.local.Forecast

@Dao
interface ForecastDao {
    @Insert
    suspend fun insert(forecast: Forecast)

    @Query(
        """
            SELECT * FROM forecast WHERE apiDeliveryDate = :apiDeliveryDate 
            AND apiDeliveryTime = :apiDeliveryTime 
            AND nx = :nx 
            AND ny = :ny
        """
    )
    suspend fun load(
        apiDeliveryDate : String,
        apiDeliveryTime : String,
        nx: Int,
        ny: Int
    ): Forecast?

    @Query("DELETE FROM forecast")
    suspend fun clear()
}
