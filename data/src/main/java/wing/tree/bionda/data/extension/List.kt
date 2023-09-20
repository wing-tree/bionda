package wing.tree.bionda.data.extension

fun <T> List<T>.mostCommon(vararg excluded: T): T? {
    return filterNot {
        it in excluded
    }.groupingBy {
        it
    }
        .eachCount()
        .maxByOrNull {
            it.value
        }?.key
}

fun <T> List<T>.updatedWith(function: MutableList<T>.() -> Unit): List<T> = toMutableList().apply(function)
