package wing.tree.bionda.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wing.tree.bionda.data.BuildConfig
import wing.tree.bionda.data.extension.apiDeliveryDate
import wing.tree.bionda.data.extension.apiDeliveryTime
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.baseTime
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.sixty
import wing.tree.bionda.data.model.DetailedFunction
import wing.tree.bionda.data.model.Result
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.forecast.Forecast
import wing.tree.bionda.data.regular.apiDeliveryCalendar
import wing.tree.bionda.data.regular.baseCalendar
import wing.tree.bionda.data.model.forecast.local.Forecast as LocalDataModel
import wing.tree.bionda.data.source.local.ForecastDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.ForecastDataSource as RemoteDataSource

class ForecastRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun getUltraSrtFcst(
        nx: Int,
        ny: Int
    ): Result<Forecast> {
        val detailedFunction = DetailedFunction.ULTRA_SRT_FCST
        val apiDeliveryCalendar = apiDeliveryCalendar(detailedFunction)
        val baseCalendar = baseCalendar(detailedFunction)

        return try {
            val forecast = localDataSource.load(
                apiDeliveryCalendar.apiDeliveryDate,
                apiDeliveryCalendar.apiDeliveryTime,
                nx,
                ny
            ) ?: remoteDataSource.getUltraSrtFcst(
                serviceKey = BuildConfig.serviceKey,
                numOfRows = Int.sixty,
                pageNo = Int.one,
                dataType = DATA_TYPE,
                baseDate = baseCalendar.baseDate,
                baseTime = baseCalendar.baseTime,
                nx = nx,
                ny = ny
            ).also {
                coroutineScope.launch {
                    with(localDataSource) {
                        clear()
                        insert(
                            it.toLocalDataModel(
                                apiDeliveryDate = apiDeliveryCalendar.apiDeliveryDate,
                                apiDeliveryTime = apiDeliveryCalendar.apiDeliveryTime,
                                nx = nx,
                                ny = ny
                            )
                        )
                    }
                }
            }

            Complete.Success(forecast)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    suspend fun getVilageFcst(
        nx: Int,
        ny: Int
    ): Result<Forecast> {
        return try {
            val detailedFunction = DetailedFunction.VILAGE_FCST
            val apiDeliveryCalendar = apiDeliveryCalendar(detailedFunction)
            val baseCalendar = baseCalendar(detailedFunction)

            val vilageFcst = localDataSource.load(
                apiDeliveryCalendar.apiDeliveryDate,
                apiDeliveryCalendar.apiDeliveryTime,
                nx,
                ny
            ) ?: remoteDataSource.getVilageFcst(
                serviceKey = BuildConfig.serviceKey,
                numOfRows = 144,
                pageNo = Int.one,
                dataType = DATA_TYPE,
                baseDate = baseCalendar.baseDate,
                baseTime = baseCalendar.baseTime,
                nx = nx,
                ny = ny
            ).also {
                coroutineScope.launch {
                    with(localDataSource) {
                        clear()
                        insert(
                            it.toLocalDataModel(
                                apiDeliveryDate = apiDeliveryCalendar.apiDeliveryDate,
                                apiDeliveryTime = apiDeliveryCalendar.apiDeliveryTime,
                                nx = nx,
                                ny = ny
                            )
                        )
                    }
                }
            }

            Complete.Success(vilageFcst)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    private fun Forecast.toLocalDataModel(
        apiDeliveryDate: String,
        apiDeliveryTime: String,
        nx: Int,
        ny: Int
    ): LocalDataModel {
        return LocalDataModel(
            items = items,
            apiDeliveryDate = apiDeliveryDate,
            apiDeliveryTime = apiDeliveryTime,
            nx = nx,
            ny = ny
        )
    }

    companion object {
        const val DATA_TYPE = "JSON"
    }
}