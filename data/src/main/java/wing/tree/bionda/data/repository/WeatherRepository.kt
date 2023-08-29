package wing.tree.bionda.data.repository

import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import wing.tree.bionda.data.PostProcessor
import wing.tree.bionda.data.extension.awaitOrFailure
import wing.tree.bionda.data.extension.julianDay
import wing.tree.bionda.data.extension.locdate
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.data.extension.roundDownToTens
import wing.tree.bionda.data.extension.tmFc
import wing.tree.bionda.data.extension.toDegreeMinute
import wing.tree.bionda.data.extension.uvIdxTime
import wing.tree.bionda.data.model.CalendarDecorator.Base
import wing.tree.bionda.data.model.DegreeMinute.Type.LATITUDE
import wing.tree.bionda.data.model.DegreeMinute.Type.LONGITUDE
import wing.tree.bionda.data.model.LCRiseSetInfo
import wing.tree.bionda.data.model.MidLandFcst
import wing.tree.bionda.data.model.MidLandFcstTa
import wing.tree.bionda.data.model.MidLandFcstTa.Companion.MidLandFcstTa
import wing.tree.bionda.data.model.MidTa
import wing.tree.bionda.data.model.RegId
import wing.tree.bionda.data.model.UVIdx
import wing.tree.bionda.data.model.UltraSrtFcst
import wing.tree.bionda.data.model.UltraSrtNcst
import wing.tree.bionda.data.model.VilageFcst
import wing.tree.bionda.data.model.core.State.Complete
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
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
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
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    suspend fun getLCRiseSetInfo(location: Location): Complete<LCRiseSetInfo.Local> {
        return try {
            val longitude = "${location.latitude.toDegreeMinute(LATITUDE)}"
            val latitude = "${location.longitude.toDegreeMinute(LONGITUDE)}"
            val params = RiseSetInfoService.Params(
                locdate = koreaCalendar.locdate,
                longitude = longitude,
                latitude = latitude
            )

            val lcRiseSetInfo = localDataSource.loadLCRiseSetInfo(
                params = params
            ) ?: remoteDataSource.getLCRiseSetInfo(
                params = params
            ).toLocal(
                secondaryLongitude = longitude,
                secondaryLatitude = latitude
            ).also {
                localDataSource.cache(it)
            }

            Complete.Success(lcRiseSetInfo)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
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
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    suspend fun getUVIdx(location: Location): Complete<UVIdx.Local> {
        return try {
            val areaNo = localDataSource.getAreaNo(location)
            val time = koreaCalendar.uvIdxTime

            val uvIdx = localDataSource.loadUVIdx(
                areaNo = areaNo,
                time = time
            ) ?: remoteDataSource.getUVIdx(
                areaNo = areaNo,
                time = time
            ).toLocal(
                areaNo = areaNo,
                time = time
            ).also {
                localDataSource.cache(it)
            }

            Complete.Success(uvIdx)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    suspend fun getUltraSrtFcst(
        nx: Int,
        ny: Int
    ): Complete<UltraSrtFcst.Local> {
        return try {
            // TODO cleanup...
            val minute = with(koreaCalendar) {
                if (minute < 5) {
                    55
                } else {
                    with(minute.plus(5).div(10)) {
                        times(10).minus(5)
                    }
                }
            }

            val params = VilageFcstInfoService.Params(
                baseCalendar = baseCalendar(Base.UltraSrtFcst),
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

            Complete.Success(ultraSrtFcst)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    suspend fun getUltraSrtNcst(
        nx: Int,
        ny: Int
    ): Complete<UltraSrtNcst.Local> {
        return try {
            val minute = koreaCalendar.minute.roundDownToTens()
            val params = VilageFcstInfoService.Params(
                baseCalendar = baseCalendar(Base.UltraSrtNcst),
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
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    suspend fun getVilageFcst(
        nx: Int,
        ny: Int
    ): Complete<VilageFcst.Local> {
        return try {
            val params = VilageFcstInfoService.Params(
                baseCalendar = baseCalendar(Base.VilageFcst),
                nx = nx,
                ny = ny
            )

            val vilageFcst = localDataSource.loadVilageFcst(
                params = params
            ) ?: remoteDataSource.getVilageFcst(
                numOfRows = 290,
                params = params
            ).let {
                with(postProcessor) {
                    it.process(params)
                }
            }

            Complete.Success(vilageFcst)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }
}
