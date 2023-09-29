package wing.tree.bionda.data.repository

import android.location.Location
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import wing.tree.bionda.data.core.DegreeMinute.Type.LATITUDE
import wing.tree.bionda.data.core.DegreeMinute.Type.LONGITUDE
import wing.tree.bionda.data.core.PartialSuccess
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.exception.MultipleExceptions
import wing.tree.bionda.data.extension.awaitOrFailure
import wing.tree.bionda.data.extension.delayDateBy
import wing.tree.bionda.data.extension.exceptions
import wing.tree.bionda.data.extension.failed
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.isSingle
import wing.tree.bionda.data.extension.julianDay
import wing.tree.bionda.data.extension.locdate
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.roundDownToTens
import wing.tree.bionda.data.extension.succeeded
import wing.tree.bionda.data.extension.three
import wing.tree.bionda.data.extension.tmFc
import wing.tree.bionda.data.extension.toBin
import wing.tree.bionda.data.extension.toDegreeMinute
import wing.tree.bionda.data.extension.values
import wing.tree.bionda.data.model.Decorator
import wing.tree.bionda.data.model.LCRiseSetInfo
import wing.tree.bionda.data.model.MidLandFcst
import wing.tree.bionda.data.model.MidLandFcstTa
import wing.tree.bionda.data.model.MidLandFcstTa.Companion.MidLandFcstTa
import wing.tree.bionda.data.model.MidTa
import wing.tree.bionda.data.model.RegId
import wing.tree.bionda.data.model.UltraSrtFcst
import wing.tree.bionda.data.model.UltraSrtNcst
import wing.tree.bionda.data.model.VilageFcst
import wing.tree.bionda.data.processor.PostProcessor
import wing.tree.bionda.data.service.RiseSetInfoService
import wing.tree.bionda.data.service.VilageFcstInfoService
import wing.tree.bionda.data.top.level.baseCalendar
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.data.top.level.tmFcCalendar
import wing.tree.bionda.data.source.local.WeatherDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.WeatherDataSource as RemoteDataSource

class WeatherRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val postProcessor: PostProcessor
) {
    private val ioDispatcher = Dispatchers.IO

    private suspend fun getLCRiseSetInfo(params: RiseSetInfoService.Params): Complete<LCRiseSetInfo.Local> {
        return try {
            val lcRiseSetInfo = localDataSource.loadLCRiseSetInfo(
                params = params
            ) ?: remoteDataSource.getLCRiseSetInfo(
                params = params
            ).toLocal(
                params = params
            ).also {
                localDataSource.cache(
                    params = params,
                    lcRiseSetInfo = it
                )
            }

            Complete.Success(lcRiseSetInfo)
        } catch (exception: Throwable) {
            Complete.Failure(exception)
        }
    }

    private suspend fun getMidLandFcst(regId: String, tmFc: String): Complete<MidLandFcst.Local> {
        return try {
            val local = localDataSource.loadMidLandFcst(
                regId = regId,
                tmFc = tmFc
            ) ?: remoteDataSource.getMidLandFcst(
                regId = regId,
                tmFc = tmFc
            ).let {
                with(postProcessor) {
                    it.process(regId = regId, tmFc = tmFc)
                }
            }

            Complete.Success(local)
        } catch (exception: Throwable) {
            Complete.Failure(exception)
        }
    }

    private suspend fun getMidTa(regId: String, tmFc: String): Complete<MidTa.Local> {
        return try {
            val local = localDataSource.loadMidTa(
                regId = regId,
                tmFc = tmFc
            ) ?: remoteDataSource.getMidTa(
                regId = regId,
                tmFc = tmFc
            ).let {
                with(postProcessor) {
                    it.process(regId = regId, tmFc = tmFc)
                }
            }

            Complete.Success(local)
        } catch (exception: Throwable) {
            Complete.Failure(exception)
        }
    }

    suspend fun getLCRiseSetInfo(location: Location): Complete<ImmutableList<LCRiseSetInfo.Local>> {
        val longitude = "${location.longitude.toDegreeMinute(LONGITUDE)}"
        val latitude = "${location.latitude.toDegreeMinute(LATITUDE)}"

        return coroutineScope {
            List(Int.three) {
                val params = RiseSetInfoService.Params(
                    locdate = koreaCalendar.delayDateBy(it).locdate,
                    longitude = longitude,
                    latitude = latitude
                )

                async {
                    getLCRiseSetInfo(params = params)
                }
            }.map {
                it.await()
            }.run {
                succeeded().values.toImmutableList() to failed().exceptions
            }.let { (succeeded, failed) ->
                when {
                    failed.isEmpty() -> Complete.Success(succeeded)
                    succeeded.isNotEmpty() -> PartialSuccess(
                        succeeded,
                        if (failed.isSingle()) {
                            failed.single()
                        } else {
                            MultipleExceptions(failed)
                        }
                    )

                    else -> Complete.Failure(MultipleExceptions(failed))
                }
            }
        }
    }

    suspend fun getMidLandFcstTa(location: Location): Complete<MidLandFcstTa> = coroutineScope {
        try {
            val tmFcCalendar = tmFcCalendar()
            val tmFc = tmFcCalendar.tmFc
            val midLandFcst = async(ioDispatcher) {
                val regId = localDataSource.getRegId(location, RegId.MidLandFcst)

                getMidLandFcst(regId = regId, tmFc = tmFc)
            }

            val midTa = async(ioDispatcher) {
                val regId = localDataSource.getRegId(location, RegId.MidTa)

                getMidTa(regId = regId, tmFc = tmFc)
            }

            val midLandFcstTa = MidLandFcstTa(
                midLandFcst = midLandFcst.awaitOrFailure(),
                midTa = midTa.awaitOrFailure(),
                tmFc = tmFc,
                julianDay = tmFcCalendar.julianDay
            )

            Complete.Success(midLandFcstTa)
        } catch (exception: Throwable) {
            Complete.Failure(exception)
        }
    }

    suspend fun getTmn(baseDate: String) = localDataSource.getTmn(baseDate = baseDate)
    suspend fun getTmx(baseDate: String) = localDataSource.getTmx(baseDate = baseDate)

    suspend fun getUltraSrtFcst(
        nx: Int,
        ny: Int
    ): Complete<UltraSrtFcst.Local> {
        return try {
            // TODO make const.
            val minute = koreaCalendar.minute.toBin(5..55, 10)
            val params = VilageFcstInfoService.Params(
                baseCalendar = baseCalendar(Decorator.Calendar.UltraSrtFcst),
                nx = nx,
                ny = ny
            )

            val ultraSrtFcst = localDataSource.loadUltraSrtFcst(
                params = params,
                minute = minute
            ) ?: remoteDataSource.getUltraSrtFcst(
                numOfRows = 60, // TODO check number, make const.
                params = params
            ).let {
                with(postProcessor) {
                    it.process(
                        params = params,
                        minute = minute
                    )
                }
            }

            Complete.Success(ultraSrtFcst.takeAfter(koreaCalendar))
        } catch (exception: Throwable) {
            Complete.Failure(exception)
        }
    }

    suspend fun getUltraSrtNcst(
        nx: Int,
        ny: Int
    ): Complete<UltraSrtNcst.Local> {
        return try {
            val minute = koreaCalendar.minute.roundDownToTens()
            val params = VilageFcstInfoService.Params(
                baseCalendar = baseCalendar(Decorator.Calendar.UltraSrtNcst),
                nx = nx,
                ny = ny
            )

            val ultraSrtNcst = localDataSource.loadUltraSrtNcst(
                params = params,
                minute = minute
            ) ?: remoteDataSource.getUltraSrtNcst(
                params = params
            ).let {
                with(postProcessor) {
                    it.process(params, minute)
                }
            }

            Complete.Success(ultraSrtNcst)
        } catch (exception: Throwable) {
            Complete.Failure(exception)
        }
    }

    suspend fun getVilageFcst(
        nx: Int,
        ny: Int
    ): Complete<VilageFcst.Local> {
        return try {
            val baseCalendar = baseCalendar(Decorator.Calendar.VilageFcst)
            val hourOfDay = baseCalendar.hourOfDay
            val params = VilageFcstInfoService.Params(
                baseCalendar = baseCalendar,
                nx = nx,
                ny = ny
            )

            // TODO, make as const. val.. etc.
            var numOfRows = 580.plus(23.minus(hourOfDay).times(12))

            if (hourOfDay in listOf(2)) {
                numOfRows += Int.one
            }

            if (hourOfDay in listOf(2, 5, 8, 11)) {
                numOfRows += Int.one
            }

            val vilageFcst = localDataSource.loadVilageFcst(
                params = params
            ) ?: remoteDataSource.getVilageFcst(
                numOfRows = numOfRows,
                params = params
            ).let {
                with(postProcessor) {
                    it.process(params)
                }
            }

            Complete.Success(vilageFcst.takeAfter(koreaCalendar))
        } catch (exception: Throwable) {
            Complete.Failure(exception)
        }
    }
}
