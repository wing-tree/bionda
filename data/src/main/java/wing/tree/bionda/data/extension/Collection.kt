package wing.tree.bionda.data.extension

import wing.tree.bionda.data.core.State

val Collection<State.Complete.Failure>.exceptions: List<Throwable> get() = map {
    it.throwable
}

val <T> Collection<State.Complete.Success<T>>.values: List<T> get() = map {
    it.value
}

fun Collection<*>.isSingle() = size == Int.single
fun Collection<*>.isPair() = size == Int.pair

fun <T> Collection<T>.containsAny(elements: Collection<T>): Boolean {
    return elements.any {
        contains(it)
    }
}

fun <T> Collection<State.Complete<T>>.failed() = filterIsInstance<State.Complete.Failure>()
fun <T> Collection<State.Complete<T>>.succeeded() = filterIsInstance<State.Complete.Success<T>>()
