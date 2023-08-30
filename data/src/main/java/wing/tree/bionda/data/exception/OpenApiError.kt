package wing.tree.bionda.data.exception

data class OpenApiError(
    val errorCode: String,
    val errorMsg: String
) : Throwable(message = errorMsg) {
    companion object {
        const val ERROR_CODE_00 = "00"
        const val ERROR_CODE_03 = "03"
    }
}
