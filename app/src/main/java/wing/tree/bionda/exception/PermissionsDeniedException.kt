package wing.tree.bionda.exception

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class PermissionsDeniedException(val permissions: ImmutableList<String>) : Exception() {
    constructor(permission: String) : this(persistentListOf(permission))
}
