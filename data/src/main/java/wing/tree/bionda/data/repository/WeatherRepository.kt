package wing.tree.bionda.data.repository

import wing.tree.bionda.data.BuildConfig
import wing.tree.bionda.data.extension.isNull
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.model.MidLandFcst
import wing.tree.bionda.data.model.MidTa
import wing.tree.bionda.data.model.Result
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
    suspend fun getMidLandFcst(regId: String): Result<MidLandFcst.Local> {
        val tmFcCalendar = TmFcCalendar()
        val tmFc = tmFcCalendar.tmFc

        return try {
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

    suspend fun getMidTa(regId: String): Result<MidTa.Local> {
        val tmFcCalendar = TmFcCalendar()
        val tmFc = tmFcCalendar.tmFc

        return try {
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

    suspend fun getVilageFcst(
        nx: Int,
        ny: Int
    ): Result<VilageFcst.Local> {
        return try {
            val baseCalendar = BaseCalendar()
            val baseDate = baseCalendar.baseDate
            val baseTime = baseCalendar.baseTime
            val vilageFcst = localDataSource.load(
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
                    localDataSource.load(
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
        const val DATA_TYPE = "JSON"
    }
}
