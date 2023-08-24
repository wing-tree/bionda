package wing.tree.bionda.data.repository

import android.icu.util.Calendar
import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import wing.tree.bionda.data.extension.advanceHourOfDayBy
import wing.tree.bionda.data.extension.awaitOrElse
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.baseTime
import wing.tree.bionda.data.extension.isNull
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.tmFc
import wing.tree.bionda.data.model.CalendarDecorator.Base
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.weather.MidLandFcst
import wing.tree.bionda.data.model.weather.MidLandFcstTa
import wing.tree.bionda.data.model.weather.MidLandFcstTa.Companion.MidLandFcstTa
import wing.tree.bionda.data.model.weather.MidTa
import wing.tree.bionda.data.model.weather.RegId
import wing.tree.bionda.data.model.weather.UltraSrtNcst
import wing.tree.bionda.data.model.weather.VilageFcst
import wing.tree.bionda.data.regular.baseCalendar
import wing.tree.bionda.data.regular.tmFcCalendar
import wing.tree.bionda.data.service.VilageFcstInfoService
import wing.tree.bionda.data.source.local.WeatherDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.WeatherDataSource as RemoteDataSource

class WeatherRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    private val ioDispatcher = Dispatchers.IO

    private suspend fun getMidLandFcst(regId: String, tmFcCalendar: Calendar): Complete<MidLandFcst.Local> {
        return try {
            val tmFc = tmFcCalendar.tmFc
            val local = localDataSource.loadMidLandFcst(regId = regId, tmFc = tmFc) ?: remoteDataSource.getMidLandFcst(
                numOfRows = Int.one,
                pageNo = Int.one,
                regId = regId,
                tmFc = tmFc
            ).let {
                with(it.toLocal(regId = regId, tmFc = tmFc)) {
                    if (it.item.rnSt3Am.isNull()) {
                        val previous = localDataSource.loadMidLandFcst(
                            regId = regId,
                            tmFc = tmFcCalendar.advanceHourOfDayBy(12).tmFc
                        )

                        prepend(previous)
                    } else {
                        this
                    }
                }
            }.also {
                localDataSource.cache(it)
            }

            Complete.Success(local)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    private suspend fun getMidTa(regId: String, tmFcCalendar: Calendar): Complete<MidTa.Local> {
        return try {
            val tmFc = tmFcCalendar.tmFc
            val local = localDataSource.loadMidTa(
                regId = regId,
                tmFc = tmFc
            ) ?: remoteDataSource.getMidTa(
                numOfRows = Int.one,
                pageNo = Int.one,
                regId = regId,
                tmFc = tmFc
            ).let { remote ->
                with(remote.toLocal(regId, tmFc)) {
                    if (item.taMin3.isNull()) {
                        val previous = localDataSource.loadMidTa(
                            regId = regId,
                            tmFc = tmFcCalendar.advanceHourOfDayBy(12).tmFc
                        )

                        prepend(previous)
                    } else {
                        this
                    }
                }
            }.also {
                localDataSource.cache(it)
            }

            Complete.Success(local)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    suspend fun getMidLandFcstTa(location: Location): Complete<MidLandFcstTa> = coroutineScope {
        try {
            val tmFcCalendar = tmFcCalendar()
            val midLandFcst = async(ioDispatcher) {
                val regId = localDataSource.getRegId(location, RegId.MidLandFcst)

                getMidLandFcst(regId = regId, tmFcCalendar = tmFcCalendar)
            }

            val midTa = async(ioDispatcher) {
                val regId = localDataSource.getRegId(location, RegId.MidTa)

                getMidTa(regId = regId, tmFcCalendar = tmFcCalendar)
            }

            val midLandFcstTa = MidLandFcstTa(
                midLandFcst = midLandFcst.awaitOrElse {
                    Complete.Failure(it)
                },
                midTa = midTa.awaitOrElse {
                    Complete.Failure(it)
                },
                tmFcCalendar = tmFcCalendar
            )

            Complete.Success(midLandFcstTa)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    suspend fun getUltraSrtNcst(
        nx: Int,
        ny: Int
    ): Complete<UltraSrtNcst.Local> {
        return try {
            val baseCalendar = baseCalendar(Base.UltraSrtNcst)
            val baseDate = baseCalendar.baseDate
            val baseTime = baseCalendar.baseTime
            val params = VilageFcstInfoService.Params(
                baseDate = baseDate,
                baseTime = baseTime,
                nx = nx,
                ny = ny
            )

            val ultraSrtNcst = localDataSource.loadUltraSrtNcst(params) ?: remoteDataSource.getUltraSrtNcst(
                numOfRows = 290,
                pageNo = Int.one,
                params
            ).let { remote ->
                // TODO caching with 10 minutes interval.
                remote.toLocal(params)
            }

            Complete.Success(ultraSrtNcst)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    suspend fun getVilageFcst(
        nx: Int,
        ny: Int
    ): Complete<VilageFcst.Local> {
        return try {
            val baseCalendar = baseCalendar(Base.VilageFcst)
            val baseDate = baseCalendar.baseDate
            val baseTime = baseCalendar.baseTime
            val params = VilageFcstInfoService.Params(
                baseDate = baseDate,
                baseTime = baseTime,
                nx = nx,
                ny = ny
            )

            val vilageFcst = localDataSource.loadVilageFcst(params) ?: remoteDataSource.getVilageFcst(
                numOfRows = 290,
                pageNo = Int.one,
                params = params
            ).let { remote ->
                val previous = baseCalendar.advanceHourOfDayBy(3).let {
                    localDataSource.loadVilageFcst(
                        params.copy(
                            baseDate = it.baseDate,
                            baseTime = it.baseTime
                        )
                    )
                }

                remote.toLocal(params).prepend(previous).also {
                    localDataSource.cache(it)
                }
            }

            Complete.Success(vilageFcst)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }
}
