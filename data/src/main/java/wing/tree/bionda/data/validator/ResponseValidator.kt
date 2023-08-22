package wing.tree.bionda.data.validator

import wing.tree.bionda.data.model.Response

interface ResponseValidator {
    val response: Response<*>

    fun validate(vararg params: String)
}
