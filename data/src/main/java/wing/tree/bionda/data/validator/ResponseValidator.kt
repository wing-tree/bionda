package wing.tree.bionda.data.validator

import wing.tree.bionda.data.exception.OpenApiError
import wing.tree.bionda.data.model.Response

object ResponseValidator {
    fun validate(response: Response<*>) {
        if (response.isUnsuccessful) {
            val header = response.header

            throw OpenApiError(
                errorCode = header.resultCode,
                errorMsg = header.resultMsg
            )
        }
    }
}
