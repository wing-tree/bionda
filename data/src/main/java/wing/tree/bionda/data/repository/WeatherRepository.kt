package wing.tree.bionda.data.repository

import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import wing.tree.bionda.data.BuildConfig
import wing.tree.bionda.data.extension.isNull
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.model.MidLandFcst
import wing.tree.bionda.data.model.MidLandFcstTa
import wing.tree.bionda.data.model.MidLandFcstTa.Companion.MidLandFcstTa
import wing.tree.bionda.data.model.MidTa
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.VilageFcst
import wing.tree.bionda.data.model.calendar.BaseCalendar
import wing.tree.bionda.data.model.calendar.TmFcCalendar
import wing.tree.bionda.data.source.local.WeatherDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.WeatherDataSource as RemoteDataSource

class WeatherRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    private val ioDispatcher = Dispatchers.IO

    private suspend fun getMidLandFcst(regId: String, tmFcCalendar: TmFcCalendar): Complete<MidLandFcst.Local> {
        return try {
            val tmFc = tmFcCalendar.tmFc
            val local = localDataSource.loadMidLandFcst(
                regId = regId,
                tmFc = tmFc
            ) ?: remoteDataSource.getMidLandFcst(
                serviceKey = BuildConfig.midFcstInfoServiceKey,
                numOfRows = Int.one,
                pageNo = Int.one,
                dataType = DATA_TYPE,
                regId = regId,
                tmFc = tmFc
            ).let {
                with(it.toLocal(tmFc = tmFc)) {
                    if (it.item.rnSt3Am.isNull()) {
                        val previous = localDataSource.loadMidLandFcst(
                            regId = regId,
                            tmFc = tmFcCalendar.previous().tmFc
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

    private suspend fun getMidTa(regId: String, tmFcCalendar: TmFcCalendar): Complete<MidTa.Local> {
        return try {
            val tmFc = tmFcCalendar.tmFc
            val local = localDataSource.loadMidTa(
                regId = regId,
                tmFc = tmFc
            ) ?: remoteDataSource.getMidTa(
                serviceKey = BuildConfig.midFcstInfoServiceKey,
                numOfRows = Int.one,
                pageNo = Int.one,
                dataType = DATA_TYPE,
                regId = regId,
                tmFc = tmFc
            ).let { remote ->
                with(remote.toLocal(tmFc)) {
                    if (item.taMin3.isNull()) {
                        val previous = localDataSource.loadMidTa(
                            regId = regId,
                            tmFc = tmFcCalendar.previous().tmFc
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
        val regId = withContext(ioDispatcher) {
            localDataSource.getRegId(location) ?: DEFAULT_REG_ID
        }

        val tmFcCalendar = TmFcCalendar()
        val midLandFcst = async {
            getMidLandFcst(regId = regId, tmFcCalendar = tmFcCalendar)
        }

        val midTa = async {
            getMidTa(regId = regId, tmFcCalendar = tmFcCalendar)
        }

        try {
            val midLandFcstTa = MidLandFcstTa(
                midLandFcst.await(),
                midTa.await()
            )

            Complete.Success(midLandFcstTa)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    suspend fun getVilageFcst(
        nx: Int,
        ny: Int
    ): Complete<VilageFcst.Local> {
        return try {
            val baseCalendar = BaseCalendar()
            val baseDate = baseCalendar.baseDate
            val baseTime = baseCalendar.baseTime
            val vilageFcst = localDataSource.loadVilageFcst(
                baseDate = baseDate,
                baseTime = baseTime,
                nx = nx,
                ny = ny
            ) ?: remoteDataSource.getVilageFcst(
                serviceKey = BuildConfig.vilageFcstInfoServiceKey,
                numOfRows = 290,
                pageNo = Int.one,
                dataType = DATA_TYPE,
                baseDate = baseDate,
                baseTime = baseTime,
                nx = nx,
                ny = ny
            ).let { remote ->
                val previous = baseCalendar.previous().let {
                    localDataSource.loadVilageFcst(
                        baseDate = it.baseDate,
                        baseTime = it.baseDate,
                        nx = nx,
                        ny = ny
                    )
                }

                remote.toLocal(
                    baseDate = baseCalendar.baseDate,
                    baseTime = baseCalendar.baseTime
                )
                    .prepend(previous)
                    .also {
                        localDataSource.cache(it)
                    }
            }

            Complete.Success(vilageFcst)
        } catch (throwable: Throwable) {
            Complete.Failure(throwable)
        }
    }

    companion object {
        private const val DATA_TYPE = "JSON"
        private const val DEFAULT_REG_ID = "11B10101"
    }
}
