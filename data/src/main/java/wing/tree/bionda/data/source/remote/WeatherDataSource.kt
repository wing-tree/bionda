package wing.tree.bionda.data.source.remote

import kotlinx.coroutines.delay
import timber.log.Timber
import wing.tree.bionda.data.extension.five
import wing.tree.bionda.data.extension.hundreds
import wing.tree.bionda.data.extension.long
import wing.tree.bionda.data.extension.three
import wing.tree.bionda.data.extension.two
import wing.tree.bionda.data.service.MidFcstInfoService
import wing.tree.bionda.data.service.VilageFcstInfoService
import kotlin.math.pow

class WeatherDataSource(
    private val midFcstInfoService: MidFcstInfoService,
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

    suspend fun getMidLandFcst(
        serviceKey: String,
        numOfRows: Int,
        pageNo: Int,
        dataType: String,
        regId: String,
        tmFc: String
    ) = retry {
        midFcstInfoService.getMidLandFcst(
            serviceKey = serviceKey,
            numOfRows = numOfRows,
            pageNo = pageNo,
            dataType = dataType,
            regId = regId,
            tmFc = tmFc
        )
    }

    suspend fun getMidTa(
        serviceKey: String,
        numOfRows: Int,
        pageNo: Int,
        dataType: String,
        regId: String,
        tmFc: String
    ) = retry {
        midFcstInfoService.getMidTa(
            serviceKey = serviceKey,
            numOfRows = numOfRows,
            pageNo = pageNo,
            dataType = dataType,
            regId = regId,
            tmFc = tmFc
        )
    }

    suspend fun getVilageFcst(
        serviceKey: String,
        numOfRows: Int,
        pageNo: Int,
        dataType: String,
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int
    ) = retry {
        vilageFcstInfoService.getVilageFcst(
            serviceKey = serviceKey,
            numOfRows = numOfRows,
            pageNo = pageNo,
            dataType = dataType,
            baseDate = baseDate,
            baseTime = baseTime,
            nx = nx,
            ny = ny
        )
    }
}
