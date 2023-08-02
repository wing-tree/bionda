package wing.tree.bionda.exception

import kotlinx.collections.immutable.ImmutableList

data class PermissionsDeniedException(val permissions: ImmutableList<String>) : Exception()
