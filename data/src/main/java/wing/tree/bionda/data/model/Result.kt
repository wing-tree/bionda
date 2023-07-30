@file:Suppress("unused")

package wing.tree.bionda.data.model

import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.Result.Loading
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed class Result<out R> {
    object Loading : Result<Nothing>()
    sealed class Complete<out R> : Result<R>() {
        data class Success<out T>(val data: T) : Complete<T>()
        data class Failure(val throwable: Throwable) : Complete<Nothing>()
    }

    val isSuccess: Boolean get() = this is Complete.Success
    val isFailure: Boolean get() = this is Complete.Failure
}


inline fun <R, T> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        Loading -> Loading
        is Complete -> when (this) {
            is Complete.Success -> Complete.Success(transform(data))
            is Complete.Failure -> Complete.Failure(throwable)
        }
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (this is Complete.Success) {
        action(data)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <T> Result<T>.onFailure(action: (exception: Throwable) -> Unit): Result<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (this is Complete.Failure) {
        action(throwable)
    }

    return this
}