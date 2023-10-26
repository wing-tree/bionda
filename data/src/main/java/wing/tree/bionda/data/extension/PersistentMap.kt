package wing.tree.bionda.data.extension

import kotlinx.collections.immutable.PersistentMap

fun <K, V> PersistentMap<K, V>.putAllIfAbsent(other: PersistentMap<K, V>): PersistentMap<K, V> {
    val builder = builder()

    for ((key, value) in other) {
        builder.putIfAbsent(key, value)
    }

    return builder.build()
}
