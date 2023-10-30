package wing.tree.bionda.permissions

import android.content.Context
import android.content.pm.PackageManager
import wing.tree.bionda.data.extension.`is`

interface PermissionChecker {
    class Result(m: Map<String, State>) : Map<String, State> by HashMap(m) {
        operator fun component1(): Set<String> = granted()
        operator fun component2(): Set<String> = denied()

        private fun granted() = filterValues(State::isGranted).keys
        private fun denied() = filterValues(State::isDenied).keys
    }

    sealed interface State {
        val shouldShowRequestPermissionRationale: Boolean

        fun isGranted() = this is Granted
        fun isDenied() = this is Denied

        data object Granted : State {
            override val shouldShowRequestPermissionRationale: Boolean = false
        }

        data class Denied(
            override val shouldShowRequestPermissionRationale: Boolean
        ) : State
    }

    fun Context.checkSelfMultiplePermissions(permissions: Array<out String>): Boolean {
        return permissions.all {
            checkSelfPermission(it) `is` PackageManager.PERMISSION_GRANTED
        }
    }

    fun Context.checkSelfSinglePermission(permission: String): Boolean {
        return checkSelfPermission(permission) `is` PackageManager.PERMISSION_GRANTED
    }
}
