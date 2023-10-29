package wing.tree.bionda.data.extension

fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> {
    val linkedHashMap = LinkedHashMap<K, V>()

    for ((key, value) in this) {
        if (value.isNotNull()) {
            linkedHashMap[key] = value
        }
    }

    return linkedHashMap
}
