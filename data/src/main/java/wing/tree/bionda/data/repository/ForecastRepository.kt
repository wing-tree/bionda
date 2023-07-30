package wing.tree.bionda.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import wing.tree.bionda.data.BuildConfig
import wing.tree.bionda.data.extension.ONE
import wing.tree.bionda.data.extension.SIXTY
import wing.tree.bionda.data.extension.requestDate
import wing.tree.bionda.data.extension.requestTime
import wing.tree.bionda.data.model.Result
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.forecast.Forecast
import wing.tree.bionda.data.regular.requestCalendar
import wing.tree.bionda.data.model.forecast.local.Forecast as LocalDataModel
import wing.tree.bionda.data.source.local.ForecastDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.ForecastDataSource as RemoteDataSource

class ForecastRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    private val supervisorScope by lazy {
        CoroutineScope(Dispatchers.IO + SupervisorJob())
    }

    suspend fun getUltraSrtFcst(
        numOfRows: Int = Int.SIXTY,
        pageNo: Int = Int.ONE,
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int
    ): Result<Forecast> {
        val requestCalendar = requestCalendar()
        val requestDate = requestCalendar.requestDate
        val requestTime = requestCalendar.requestTime

        return try {
            val forecast = localDataSource.load(
                requestDate,
                requestTime
            ) ?: remoteDataSource.getUltraSrtFcst(
                serviceKey = BuildConfig.serviceKey,
                numOfRows = numOfRows,
                pageNo = pageNo,
                dataType = DATA_TYPE,
                baseDate = baseDate,
                baseTime = baseTime,
                nx = nx,
                ny = ny
            ).also {
                supervisorScope.launch {
                    with(localDataSource) {
                        clear()
                        insert(
                            it.toLocalDataModel(
                                requestDate,
                                requestTime
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
        serviceKey: String,
        numOfRows: Int = Int.SIXTY,
        pageNo: Int = Int.ONE,
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int
    ): Result<Forecast> {
        return try {
            val vilageFcst = remoteDataSource.getVilageFcst(
                serviceKey = serviceKey,
                numOfRows = numOfRows,
                pageNo = pageNo,
                dataType = DATA_TYPE,
                baseDate = baseDate,
                baseTime = baseTime,
                nx = nx,
                ny = ny
            )

            Complete.Success(vilageFcst)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    private fun Forecast.toLocalDataModel(
        requestDate: String,
        requestTime: String
    ): LocalDataModel {
        return LocalDataModel(
            items = items,
            requestDate = requestDate,
            requestTime = requestTime
        )
    }

    companion object {
        const val DATA_TYPE = "JSON"
    }
}
