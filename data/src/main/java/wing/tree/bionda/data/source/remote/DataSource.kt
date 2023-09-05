package wing.tree.bionda.data.source.remote

import kotlinx.coroutines.delay
import timber.log.Timber
import wing.tree.bionda.data.extension.five
import wing.tree.bionda.data.extension.hundreds
import wing.tree.bionda.data.extension.long
import wing.tree.bionda.data.extension.two
import kotlin.math.pow

abstract class DataSource {
    protected suspend fun <T> retry(
        retries: Int = Int.two,
        initialDelay: Long = Long.five.hundreds,
        block: suspend () -> T
    ): T {
        repeat(retries) { attempt ->
            try {
                return block()
            } catch (cause: Throwable) {
                Timber.e(cause)
            }

            delay(initialDelay.times(Double.two.pow(attempt)).long)
        }

        return block()
    }
}