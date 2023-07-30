package wing.tree.bionda.data.model.forecast

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val baseDate: Int,
    val baseTime: Int,
    val category: String,
    val fcstDate : Int,
    val fcstTime : Int,
    val fcstValue : String,
    val nx : Int,
    val ny : Int
)
