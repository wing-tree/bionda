package wing.tree.bionda.data.validator

import wing.tree.bionda.data.core.Response
import wing.tree.bionda.data.exception.OpenApiError

interface ResponseValidator<T, R> {
    val response: Response<T>

    val errorCode: String get() = response.header.resultCode
    val isUnsuccessful: Boolean get() = response.isUnsuccessful

    suspend fun validate(
        errorMsg: (Response<T>) -> String,
        ifInvalid: (suspend (OpenApiError) -> R)? = null
    ): R

    suspend fun validate(
        `this`: R,
        errorMsg: (Response<T>) -> String,
        ifInvalid: (suspend (OpenApiError) -> R)? = null
    ): R {
        if (isUnsuccessful) {
            val openApiError = OpenApiError(
                errorCode = errorCode,
                errorMsg = errorMsg(response)
            )

            return ifInvalid?.invoke(openApiError) ?: throw openApiError
        }

        return `this`
    }
}
