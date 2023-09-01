package wing.tree.bionda.data.exception

import wing.tree.bionda.data.extension.empty

class MultipleExceptions(exceptions: List<Throwable>) : Exception(
    exceptions.joinToString {
        it.message ?: String.empty
    }
)
