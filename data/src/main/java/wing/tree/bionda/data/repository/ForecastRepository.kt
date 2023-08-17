package wing.tree.bionda.data.repository

import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wing.tree.bionda.data.BuildConfig
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.model.BaseCalendar
import wing.tree.bionda.data.model.Result
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.forecast.Forecast
import wing.tree.bionda.data.regular.koreaCalendar
import wing.tree.bionda.data.model.forecast.local.Forecast as LocalDataModel
import wing.tree.bionda.data.source.local.ForecastDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.ForecastDataSource as RemoteDataSource

class ForecastRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private fun cache(localDataModel: LocalDataModel) {
        coroutineScope.launch {
            with(localDataSource) {
                clear()
                insert(localDataModel)
            }
        }
    }

    suspend fun get(
        nx: Int,
        ny: Int
    ): Result<Forecast> {
        return try {
            val baseCalendar = BaseCalendar()
            val baseDate = baseCalendar.baseDate
            val baseTime = baseCalendar.baseTime
            val forecast = localDataSource.load(
                baseDate = baseDate,
                baseTime = baseTime,
                nx = nx,
                ny = ny
            ) ?: remoteDataSource.get(
                serviceKey = BuildConfig.serviceKey,
                numOfRows = 290,
                pageNo = Int.one,
                dataType = DATA_TYPE,
                baseDate = baseDate,
                baseTime = baseTime,
                nx = nx,
                ny = ny
            ).let {
                val previousForecast = with(baseCalendar.previous()) {
                    localDataSource.load(
                        baseDate = this.baseDate,
                        baseTime = this.baseDate,
                        nx = nx,
                        ny = ny
                    )
                }

                val localDataModel = it.toLocalDataModel(
                    previousForecast = previousForecast,
                    baseDate = baseDate,
                    baseTime = baseTime,
                    nx = nx,
                    ny = ny
                )

                cache(localDataModel)

                localDataModel
            }

            Complete.Success(forecast)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    private fun Forecast.toLocalDataModel(
        previousForecast: LocalDataModel?,
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int
    ): LocalDataModel {
        val koreaCalendar = koreaCalendar()
        val previousItems = previousForecast?.items?.filter {
            false // TODO. 베이스 date/time 받아서 캘린더 생성하는 함수 필요. 해당 캘린더와 3시간 차이나는 케이스. 필터링.
        } ?: emptyList()

        return LocalDataModel(
            items = items.plus(previousItems).toImmutableList(),
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
