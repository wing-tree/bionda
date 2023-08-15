package wing.tree.bionda.data.model.forecast

interface Forecast {
    val items: List<Item>
    val nx: Int
    val ny: Int
}
