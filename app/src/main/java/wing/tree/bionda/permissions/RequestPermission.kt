package wing.tree.bionda.permissions

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import wing.tree.bionda.data.extension.`is`

typealias Result = Map<String, RequestPermission.State>

interface RequestPermission : PermissionChecker {
    sealed interface State {
        val shouldShowRequestPermissionRationale: Boolean

        object Granted : State {
            override val shouldShowRequestPermissionRationale: Boolean = false
        }

        data class Denied(
            override val shouldShowRequestPermissionRationale: Boolean
        ) : State
    }

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
        }
    }

    private fun ComponentActivity.shouldShowRequestMultiplePermissionsRationale(
        permissions: Array<String>
    ): Result {
        return permissions.associateWith {
            State.Denied(shouldShowRequestPermissionRationale(it))
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

    fun Result.denied() = filterValues {
        it is State.Denied
    }
        .keys

    fun Result.granted() = filterValues {
        it is State.Granted
    }
        .keys
}
