package wing.tree.bionda.permissions

import android.content.Context
import android.content.pm.PackageManager

interface PermissionChecker {
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
