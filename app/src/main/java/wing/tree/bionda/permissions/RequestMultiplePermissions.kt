package wing.tree.bionda.permissions

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.permissions.PermissionChecker.Result
import wing.tree.bionda.permissions.PermissionChecker.State

interface RequestMultiplePermissions : PermissionChecker {
    val launcher: ActivityResultLauncher<Array<String>>
    val permissions: Array<String>

    fun onCheckSelfMultiplePermissions(result: Result)
    fun onRequestMultiplePermissionsResult(result: Result)
    fun onShouldShowRequestMultiplePermissionsRationale(result: Result)

    private fun ComponentActivity.checkSelfMultiplePermissions(
        permissions: Array<String>
    ): Result {
        return permissions.associateWith {
            if (checkSelfPermission(it) `is` PackageManager.PERMISSION_GRANTED) {
                State.Granted
            } else {
                State.Denied(shouldShowRequestPermissionRationale(it))
            }
        }.let {
            Result(it)
        }
    }

    private fun ComponentActivity.shouldShowRequestMultiplePermissionsRationale(
        permissions: Array<String>
    ): Result {
        return permissions.associateWith {
            State.Denied(shouldShowRequestPermissionRationale(it))
        }.let {
            Result(it)
        }
    }

    fun ComponentActivity.registerForActivityResult() = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        onRequestMultiplePermissionsResult(
            it.mapValues { (key, value) ->
                if (value) {
                    State.Granted
                } else {
                    State.Denied(shouldShowRequestPermissionRationale(key))
                }
            }.let { m ->
                Result(m)
            }
        )
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
}
