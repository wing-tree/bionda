package wing.tree.bionda.data.core

import kotlinx.serialization.Serializable
import wing.tree.bionda.data.exception.OpenApiError
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.not
import wing.tree.bionda.data.extension.zero

@Serializable
data class Response<T>(
    val header: Header = Header(),
    val body: Body<T> = Body()
) {
    val isUnsuccessful: Boolean get() = header.resultCode not OpenApiError.ERROR_CODE_00
    val items: Items<T> get() = body.items
}

@Serializable
data class Header(
    val resultCode: String = String.empty,
    val resultMsg: String = String.empty
)

@Serializable
data class Body<T>(
    val dataType: String = String.empty,
    val items: Items<T> = Items(),
    val numOfRows: Int = Int.zero,
    val pageNo: Int = Int.zero,
    val totalCount: Int = Int.zero
)

@Serializable
data class Items<T>(
    val item: List<T> = emptyList()
) : List<T> by item
