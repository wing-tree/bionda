package wing.tree.bionda.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import wing.tree.bionda.data.model.forecast.local.Forecast

@Dao
interface ForecastDao {
    @Insert
    suspend fun insert(forecast: Forecast)

    @Query("SELECT * FROM forecast WHERE requestDate = :requestDate AND requestTime = :requestTime")
    suspend fun load(requestDate : String, requestTime : String): Forecast?

    @Query("DELETE FROM forecast")
    suspend fun clear()
}
