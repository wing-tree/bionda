package wing.tree.bionda.data.service

import android.icu.util.Calendar
import retrofit2.http.GET
import retrofit2.http.Query
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.baseTime
import wing.tree.bionda.data.model.UltraSrtFcst
import wing.tree.bionda.data.model.UltraSrtNcst
import wing.tree.bionda.data.model.VilageFcst

interface VilageFcstInfoService {
    data class Params(
        val baseCalendar: Calendar,
        val nx: Int,
        val ny: Int
    ) {
        val baseDate = baseCalendar.baseDate
        val baseTime = baseCalendar.baseTime
    }

    @GET("getUltraSrtFcst")
    suspend fun getUltraSrtFcst(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("dataType") dataType: String,
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): UltraSrtFcst.Remote

    @GET("getUltraSrtNcst")
    suspend fun getUltraSrtNcst(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("dataType") dataType: String,
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): UltraSrtNcst.Remote

    @GET("getVilageFcst")
    suspend fun getVilageFcst(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("dataType") dataType: String,
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): VilageFcst.Remote
}
