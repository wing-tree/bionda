package wing.tree.bionda.data.source.remote

import wing.tree.bionda.data.service.ForecastService

class ForecastDataSource(private val forecastService: ForecastService) {
    suspend fun getUltraSrtFcst(
        serviceKey: String,
        numOfRows: Int,
        pageNo: Int,
        dataType: String,
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int
    ) = forecastService.getUltraSrtFcst(
        serviceKey,
        numOfRows,
        pageNo,
        dataType,
        baseDate,
        baseTime,
        nx,
        ny
    )

    suspend fun getVilageFcst(
        serviceKey: String,
        numOfRows: Int,
        pageNo: Int,
        dataType: String,
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int
    ) = forecastService.getVilageFcst(
        serviceKey,
        numOfRows,
        pageNo,
        dataType,
        baseDate,
        baseTime,
        nx,
        ny
    )
}
