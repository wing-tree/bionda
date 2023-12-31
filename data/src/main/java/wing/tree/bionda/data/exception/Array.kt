package wing.tree.bionda.data.exception

import wing.tree.bionda.data.extension.four
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.three
import wing.tree.bionda.data.extension.two

fun <T> Array<out T>.fifth() = get(Int.four)
fun <T> Array<out T>.fourth() = get(Int.three)
fun <T> Array<out T>.second() = get(Int.one)
fun <T> Array<out T>.third() = get(Int.two)
