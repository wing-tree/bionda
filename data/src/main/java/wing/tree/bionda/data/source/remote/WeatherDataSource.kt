package wing.tree.bionda.data.source.remote

import kotlinx.coroutines.delay
import timber.log.Timber
import wing.tree.bionda.data.BuildConfig
import wing.tree.bionda.data.extension.eight
import wing.tree.bionda.data.extension.five
import wing.tree.bionda.data.extension.hundreds
import wing.tree.bionda.data.extension.long
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.three
import wing.tree.bionda.data.extension.two
import wing.tree.bionda.data.model.DataType
import wing.tree.bionda.data.service.MidFcstInfoService
import wing.tree.bionda.data.service.RiseSetInfoService
import wing.tree.bionda.data.service.VilageFcstInfoService
import kotlin.math.pow

class WeatherDataSource(
    private val midFcstInfoService: MidFcstInfoService,
    private val riseSetInfoService: RiseSetInfoService,
    private val vilageFcstInfoService: VilageFcstInfoService
) {
    private suspend fun <T> retry(
        retries: Int = Int.three,
        initialDelay: Long = Long.five.hundreds,
        block: suspend () -> T
    ): T {
        repeat(retries.dec()) { attempt ->
            try {
                return block()
            } catch (cause: Throwable) {
                Timber.e(cause)
            }

            delay(initialDelay.times(Double.two.pow(attempt)).long)
        }

        return block()
    }

    suspend fun getLCRiseSetInfo(
        params: RiseSetInfoService.Params
    ) = retry {
        with(params) {
            riseSetInfoService.getLCRiseSetInfo(
                serviceKey = BuildConfig.riseSetInfoServiceKey,
                locdate = locdate,
                longitude = longitude,
                latitude = latitude,
                dnYn = dnYn
            )
        }
    }

    suspend fun getMidLandFcst(
        numOfRows: Int = Int.one,
        pageNo: Int = Int.one,
        regId: String,
        tmFc: String
    ) = retry {
        midFcstInfoService.getMidLandFcst(
            serviceKey = BuildConfig.midFcstInfoServiceKey,
            numOfRows = numOfRows,
            pageNo = pageNo,
            dataType = DataType.JSON,
            regId = regId,
            tmFc = tmFc
        )
    }

    suspend fun getMidTa(
        numOfRows: Int = Int.one,
        pageNo: Int = Int.one,
        regId: String,
        tmFc: String
    ) = retry {
        midFcstInfoService.getMidTa(
            serviceKey = BuildConfig.midFcstInfoServiceKey,
            numOfRows = numOfRows,
            pageNo = pageNo,
            dataType = DataType.JSON,
            regId = regId,
            tmFc = tmFc
        )
    }

    suspend fun getUltraSrtNcst(
        numOfRows: Int = Int.eight,
        pageNo: Int = Int.one,
        params: VilageFcstInfoService.Params
    ) = retry {
        with(params) {
            vilageFcstInfoService.getUltraSrtNcst(
                serviceKey = BuildConfig.vilageFcstInfoServiceKey,
                numOfRows = numOfRows,
                pageNo = pageNo,
                dataType = DataType.JSON,
                baseDate = baseDate,
                baseTime = baseTime,
                nx = nx,
                ny = ny
            )
        }
    }

    suspend fun getVilageFcst(
        numOfRows: Int,
        pageNo: Int = Int.one,
        params: VilageFcstInfoService.Params
    ) = retry {
        with(params) {
            vilageFcstInfoService.getVilageFcst(
                serviceKey = BuildConfig.vilageFcstInfoServiceKey,
                numOfRows = numOfRows,
                pageNo = pageNo,
                dataType = DataType.JSON,
                baseDate = baseDate,
                baseTime = baseTime,
                nx = nx,
                ny = ny
            )
        }
    }
}
