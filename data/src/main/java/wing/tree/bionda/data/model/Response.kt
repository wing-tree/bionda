package wing.tree.bionda.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val header: Header,
    val body: Body<T>
)

@Serializable
data class Header(
    val resultCode: Int,
    val resultMsg: String
)

@Serializable
data class Body<T>(
    val dataType: String,
    val items: Items<T>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

@Serializable
data class Items<T>(
    val item: List<T>
)
