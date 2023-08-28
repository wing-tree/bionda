package wing.tree.bionda.data.service

import retrofit2.http.GET
import retrofit2.http.Query
import wing.tree.bionda.data.model.UVIdx

interface LivingWthrIdxService {
    @GET("getUVIdxV4")
    suspend fun getUVIdx(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("dataType") dataType: String,
        @Query("areaNo") areaNo: String,
        @Query("time") time: String
    ): UVIdx.Remote
}
