package wing.tree.bionda.data.extension

import kotlinx.collections.immutable.PersistentMap

fun <K, V> PersistentMap<K, V>.putAllIfAbsent(m: Map<K, V>): PersistentMap<K, V> {
    val builder = builder()

    for ((key, value) in m) {
        builder.putIfAbsent(key, value)
    }

    return builder.build()
}
