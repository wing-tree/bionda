package wing.tree.bionda.data.extension

import kotlinx.coroutines.Deferred

suspend fun <T> Deferred<T>.awaitOrElse(defaultValue: (Exception) -> T) = try {
    await()
} catch (exception: Exception) {
    defaultValue(exception)
}
