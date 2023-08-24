package wing.tree.bionda.data.model.weather

import kotlinx.serialization.Serializable
import wing.tree.bionda.data.exception.OpenApiError
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.not
import wing.tree.bionda.data.extension.zero

@Serializable
data class Response<T>(
    val header: Header,
    val body: Body<T> = Body.nothing()
) {
    val isUnsuccessful: Boolean get() = header.resultCode not OpenApiError.ERROR_CODE_00
}

@Serializable
data class Header(
    val resultCode: String,
    val resultMsg: String
)

@Serializable
data class Body<T>(
    val dataType: String,
    val items: Items<T>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
) {
    companion object {
        fun <T> nothing() = Body<T>(
            dataType = String.empty,
            items = Items(),
            numOfRows = Int.zero,
            pageNo = Int.zero,
            totalCount = Int.zero
        )
    }
}

@Serializable
data class Items<T>(
    val item: List<T> = emptyList()
)
