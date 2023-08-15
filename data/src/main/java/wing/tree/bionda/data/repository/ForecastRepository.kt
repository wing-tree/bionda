package wing.tree.bionda.data.repository

import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wing.tree.bionda.data.BuildConfig
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.baseTime
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.model.Result
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.forecast.Forecast
import wing.tree.bionda.data.regular.baseCalendar
import wing.tree.bionda.data.model.forecast.local.Forecast as LocalDataModel
import wing.tree.bionda.data.source.local.ForecastDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.ForecastDataSource as RemoteDataSource

class ForecastRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun getVilageFcst(
        nx: Int,
        ny: Int
    ): Result<Forecast> {
        return try {
            val baseCalendar = baseCalendar()
            val vilageFcst = localDataSource.load(
                baseCalendar.baseDate,
                baseCalendar.baseTime,
                nx,
                ny
            ) ?: remoteDataSource.getVilageFcst(
                serviceKey = BuildConfig.serviceKey,
                numOfRows = 290,
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
                                baseDate = baseCalendar.baseDate,
                                baseTime = baseCalendar.baseTime,
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
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int
    ): LocalDataModel {
        return LocalDataModel(
            items = items.toImmutableList(),
            baseDate = baseDate,
            baseTime = baseTime,
            nx = nx,
            ny = ny
        )
    }

    companion object {
        const val DATA_TYPE = "JSON"
    }
}
