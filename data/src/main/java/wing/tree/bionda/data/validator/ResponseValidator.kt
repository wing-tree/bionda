package wing.tree.bionda.data.validator

import wing.tree.bionda.data.core.Response

interface ResponseValidator {
    val response: Response<*>

    fun validate(vararg params: String)
}
