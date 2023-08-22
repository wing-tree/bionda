@file:Suppress("unused")

package wing.tree.bionda.data.model

import wing.tree.bionda.data.extension.isNull
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.Result.Loading
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed class Result<out R> {
    object Loading : Result<Nothing>()
    sealed class Complete<out R> : Result<R>() {
        data class Success<out T>(val value: T) : Complete<T>()
        data class Failure(val throwable: Throwable) : Complete<Nothing>()
    }

    val isSuccess: Boolean get() = this is Complete.Success
    val isFailure: Boolean get() = this is Complete.Failure
}


inline fun <R, T> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        Loading -> Loading
        is Complete -> when (this) {
            is Complete.Success -> Complete.Success(transform(value))
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
        action(value)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <T> Result<T>.onFailure(action: (throwable: Throwable) -> Unit): Result<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (this is Complete.Failure) {
        action(throwable)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <T> Complete<T>.ifFailure(defaultValue: (throwable: Throwable) -> Complete<T>): Complete<T> {
    contract {
        callsInPlace(defaultValue, InvocationKind.AT_MOST_ONCE)
    }

    if (this is Complete.Failure) {
        return defaultValue(throwable)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
fun <T> Complete<T>.isFailure(): Boolean {
    contract {
        returns(true) implies (this@isFailure is Complete.Failure)
        returns(false) implies (this@isFailure is Complete.Success<T>)
    }

    return this is Complete.Failure
}

@OptIn(ExperimentalContracts::class)
inline fun <T> Complete<T>.ifNull(defaultValue: () -> Complete<T>): Complete<T> {
    contract {
        callsInPlace(defaultValue, InvocationKind.AT_MOST_ONCE)
    }

    if (this is Complete.Success) {
        if (value.isNull()) {
            return defaultValue()
        }
    }

    return this
}

@OptIn(ExperimentalContracts::class)
fun <T> Complete<T>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Complete.Success<T>)
        returns(false) implies (this@isSuccess is Complete.Failure)
    }

    return this is Complete.Success<T>
}
