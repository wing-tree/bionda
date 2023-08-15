package wing.tree.bionda.data.source.remote

import kotlinx.coroutines.delay
import timber.log.Timber
import wing.tree.bionda.data.extension.five
import wing.tree.bionda.data.extension.hundreds
import wing.tree.bionda.data.extension.long
import wing.tree.bionda.data.extension.three
import wing.tree.bionda.data.extension.two
import wing.tree.bionda.data.service.ForecastService
import kotlin.math.pow

class ForecastDataSource(private val forecastService: ForecastService) {
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

    suspend fun get(
        serviceKey: String,
        numOfRows: Int,
        pageNo: Int,
        dataType: String,
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int
    ) = retry {
        forecastService.get(
            serviceKey,
            numOfRows,
            pageNo,
            dataType,
            baseDate,
            baseTime,
            nx,
            ny
        )
    }
}
