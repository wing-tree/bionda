package wing.tree.bionda.data.repository

import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import wing.tree.bionda.data.PostProcessor
import wing.tree.bionda.data.extension.awaitOrFailure
import wing.tree.bionda.data.extension.cloneAsCalendar
import wing.tree.bionda.data.extension.degreeMinute
import wing.tree.bionda.data.extension.locdate
import wing.tree.bionda.data.extension.tmFc
import wing.tree.bionda.data.model.CalendarDecorator.Base
import wing.tree.bionda.data.model.State.Complete
import wing.tree.bionda.data.model.weather.LCRiseSetInfo
import wing.tree.bionda.data.model.weather.MidLandFcst
import wing.tree.bionda.data.model.weather.MidLandFcstTa
import wing.tree.bionda.data.model.weather.MidLandFcstTa.Companion.MidLandFcstTa
import wing.tree.bionda.data.model.weather.MidTa
import wing.tree.bionda.data.model.weather.RegId
import wing.tree.bionda.data.model.weather.UltraSrtNcst
import wing.tree.bionda.data.model.weather.VilageFcst
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
                    it.postProcess(regId = regId, tmFc = tmFc)
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
                    it.postProcess(regId = regId, tmFc = tmFc)
                }
            }

            Complete.Success(local)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    suspend fun getLCRiseSetInfo(location: Location): Complete<LCRiseSetInfo.Local> {
        return try {
            val params = with(location) {
                RiseSetInfoService.Params(
                    locdate = koreaCalendar().locdate,
                    longitude = longitude.degreeMinute.int,
                    latitude = latitude.degreeMinute.int
                )
            }

            val lcRiseSetInfo = localDataSource.loadLCRiseSetInfo(
                params = params
            ) ?: remoteDataSource.getLCRiseSetInfo(
                params = params
            ).toLocal().also {
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
                tmFcCalendar = tmFcCalendar.cloneAsCalendar()
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
            val params = VilageFcstInfoService.Params(
                baseCalendar = baseCalendar,
                nx = nx,
                ny = ny
            )

            val ultraSrtNcst = localDataSource.loadUltraSrtNcst(params) ?: remoteDataSource.getUltraSrtNcst(
                params = params
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
            val params = VilageFcstInfoService.Params(
                baseCalendar = baseCalendar,
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
                    it.postProcess(params)
                }
            }

            Complete.Success(vilageFcst)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }
}
