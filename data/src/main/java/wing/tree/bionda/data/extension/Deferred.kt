package wing.tree.bionda.data.extension

import kotlinx.coroutines.Deferred
import wing.tree.bionda.data.core.State.Complete

suspend fun <T> Deferred<T>.awaitOrElse(defaultValue: (Exception) -> T) = try {
    await()
} catch (exception: Exception) {
    defaultValue(exception)
}

suspend fun <T> Deferred<Complete<T>>.awaitOrFailure(): Complete<T> =
    awaitOrElse {
        Complete.Failure(it)
    }
