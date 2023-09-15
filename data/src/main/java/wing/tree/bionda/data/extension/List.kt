package wing.tree.bionda.data.extension

fun <T> List<T>.updatedWith(function: MutableList<T>.() -> Unit): List<T> = toMutableList().apply(function)
