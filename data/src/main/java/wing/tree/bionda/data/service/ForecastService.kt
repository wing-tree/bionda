package wing.tree.bionda.data.service

import retrofit2.http.GET
import retrofit2.http.Query
import wing.tree.bionda.data.model.forecast.remote.Forecast

interface ForecastService {
    @GET("getVilageFcst")
    suspend fun get(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("dataType") dataType: String,
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): Forecast
}
