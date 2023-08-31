package wing.tree.bionda.data.exception

import wing.tree.bionda.data.extension.`is`

data class OpenAPIError(
    val errorCode: String,
    val errorMsg: String
) : Throwable(message = errorMsg) {
    val isErrorCode03: Boolean get() = `is`(ERROR_CODE_03)

    companion object {
        const val ERROR_CODE_00 = "00"
        const val ERROR_CODE_03 = "03"
    }
}
