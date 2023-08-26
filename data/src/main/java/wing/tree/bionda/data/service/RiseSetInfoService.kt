package wing.tree.bionda.data.service

import retrofit2.http.GET
import retrofit2.http.Query
import wing.tree.bionda.data.constant.N
import wing.tree.bionda.data.model.weather.LCRiseSetInfo

interface RiseSetInfoService {
    data class Params(
        val locdate: String,
        val longitude: Int,
        val latitude: Int,
        val dnYn: String = N
    )

    @GET("getLCRiseSetInfo")
    suspend fun getLCRiseSetInfo(
        @Query("serviceKey") serviceKey: String,
        @Query("locdate") locdate: String,
        @Query("longitude") longitude: Int,
        @Query("latitude") latitude: Int,
        @Query("dnYn") dnYn: String
    ): LCRiseSetInfo.Remote
}
