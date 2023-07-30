package wing.tree.bionda.permissions

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

typealias Result = Map<String, Boolean>

interface RequestMultiplePermissions : MultiplePermissionsChecker {
    val launcher: ActivityResultLauncher<Array<String>>
    val permissions: Array<String>

    fun onCheckSelfMultiplePermissions(result: Map<String, Boolean>)
    fun onRequestMultiplePermissionsResult(result: Map<String, Boolean>)
    fun onShouldShowRequestMultiplePermissionsRationale(result: Map<String, Boolean>)

    private fun ComponentActivity.checkSelfMultiplePermissions(
        permissions: Array<String>
    ): Result {
        return permissions.associateWith {
            checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun ComponentActivity.shouldShowRequestMultiplePermissionsRationale(
        permissions: Array<String>
    ): Result {
        return permissions.associateWith {
            shouldShowRequestPermissionRationale(it)
        }
    }

    fun ComponentActivity.registerForActivityResult() = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        onRequestMultiplePermissionsResult(it)
    }

    fun ComponentActivity.requestMultiplePermissions() {
        var permissions = checkSelfMultiplePermissions(permissions).also {
            onCheckSelfMultiplePermissions(it)
        }
            .denied()
            .toTypedArray()

        if (permissions.isNotEmpty()) {
            permissions = shouldShowRequestMultiplePermissionsRationale(permissions).also {
                onShouldShowRequestMultiplePermissionsRationale(it)
            }
                .denied()
                .toTypedArray()

            if (permissions.isNotEmpty()) {
                launcher.launch(permissions)
            }
        }
    }

    fun Result.denied() = filterValues {
        it.not()
    }
        .keys

    fun Result.granted() = filterValues {
        it
    }
        .keys
}
