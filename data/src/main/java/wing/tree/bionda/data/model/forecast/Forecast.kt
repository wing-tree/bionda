package wing.tree.bionda.data.model.forecast

import wing.tree.bionda.data.model.Category

interface Forecast {
    val items: List<Item>

    val pty: List<Item> get() = items.filter {
        it.category == Category.UltraSrtFcst.PTY
    }
}
