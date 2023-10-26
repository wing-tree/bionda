package wing.tree.bionda.data.extension

fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> {
    val hashMap = HashMap<K, V>()

    for ((key, value) in this) {
        if (value.isNotNull()) {
            hashMap[key] = value
        }
    }

    return hashMap
}
