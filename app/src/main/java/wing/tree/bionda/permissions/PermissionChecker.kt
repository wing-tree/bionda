package wing.tree.bionda.permissions

import android.content.Context
import android.content.pm.PackageManager

interface PermissionChecker {
    class Result(m: Map<String, State>) : Map<String, State> by HashMap(m) {
        operator fun component1(): Set<String> = granted()
        operator fun component2(): Set<String> = denied()

        fun denied() = filterValues {
            it is State.Denied
        }
            .keys

        fun granted() = filterValues {
            it is State.Granted
        }
            .keys
    }

    sealed interface State {
        val shouldShowRequestPermissionRationale: Boolean

        object Granted : State {
            override val shouldShowRequestPermissionRationale: Boolean = false
        }

        data class Denied(
            override val shouldShowRequestPermissionRationale: Boolean
        ) : State
    }

    fun Context.checkSelfMultiplePermissions(permissions: Array<out String>): Boolean {
        return permissions.all {
            checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun Context.checkSelfPermission(vararg permissions: String): Boolean {
        return checkSelfMultiplePermissions(permissions)
    }

    fun Context.checkSelfSinglePermission(permission: String): Boolean {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}
