@file:Suppress("unused")

package wing.tree.bionda.data.core

import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.core.State.Loading
import wing.tree.bionda.data.extension.isNull
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed class State<out R> {
    object Loading : State<Nothing>()
    sealed class Complete<out R> : State<R>() {
        open class Success<out T>(val value: T) : Complete<T>()
        open class Failure(val exception: Throwable) : Complete<Nothing>()
    }

    val isSuccess: Boolean get() = this is Complete.Success
    val isFailure: Boolean get() = this is Complete.Failure
}

class PartialSuccess<T>(
    value: T,
    val exception: Throwable,
) : Complete.Success<T>(value)

fun <T> State<T>.getOrNull() = when {
    isSuccess() -> value
    else -> null
}

@OptIn(ExperimentalContracts::class)
fun <T> State<T>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Complete.Success<T>)
        returns(false) implies (this@isSuccess is Complete.Failure)
    }

    return this is Complete.Success<T>
}

inline fun <R, T> State<T>.flatMap(transform: (T) -> State<R>): State<R> {
    return when (this) {
        Loading -> Loading
        is Complete -> when (this) {
            is Complete.Success -> transform(value)
            is Complete.Failure -> Complete.Failure(exception)
        }
    }
}

inline fun <R, T> State<T>.map(transform: (T) -> R): State<R> {
    return when (this) {
        Loading -> Loading
        is Complete -> when (this) {
            is Complete.Success -> Complete.Success(transform(value))
            is Complete.Failure -> Complete.Failure(exception)
        }
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <T> State<T>.onSuccess(action: (value: T) -> Unit): State<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (this is Complete.Success) {
        action(value)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <T> State<T>.onFailure(action: (exception: Throwable) -> Unit): State<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    if (this is Complete.Failure) {
        action(exception)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <T> Complete<T>.ifFailure(defaultValue: (exception: Throwable) -> Complete<T>): Complete<T> {
    contract {
        callsInPlace(defaultValue, InvocationKind.AT_MOST_ONCE)
    }

    if (isFailure()) {
        return defaultValue(exception)
    }

    return this
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
inline fun <T> Complete<T>.ifFailureOrNull(defaultValue: (Throwable?) -> Complete<T>): Complete<T> {
    contract {
        callsInPlace(defaultValue, InvocationKind.AT_MOST_ONCE)
    }

    return when {
        isFailure() -> defaultValue(exception)
        isSuccess() -> defaultValue(null)
        else -> this
    }
}

inline fun <R, T> Complete<T>.map(transform: (T) -> R): Complete<R> {
    return when (this) {
        is Complete.Success -> Complete.Success(transform(value))
        is Complete.Failure -> Complete.Failure(exception)
    }
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
fun <T> Complete<T>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Complete.Success<T>)
        returns(false) implies (this@isSuccess is Complete.Failure)
    }

    return this is Complete.Success<T>
}
