package wing.tree.bionda.data.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.zip
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.core.State.Loading

inline fun <R, T> Flow<State<T>>.flatMap(crossinline transform: suspend (T) -> State<R>): Flow<State<R>> = transform {
    val value = when (it) {
        Loading -> Loading
        is Complete -> when (it) {
            is Complete.Success -> transform(it.value)
            is Complete.Failure -> Complete.Failure(it.exception)
        }
    }

    emit(value)
}

fun <T1, T2> Flow<T1>.zipAsPair(other: Flow<T2>) = zip(other, ::Pair)
