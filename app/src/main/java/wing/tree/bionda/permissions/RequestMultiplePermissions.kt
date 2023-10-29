package wing.tree.bionda.permissions

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import kotlinx.coroutines.channels.Channel
import wing.tree.bionda.data.extension.single
import wing.tree.bionda.permissions.PermissionChecker.Result
import wing.tree.bionda.permissions.PermissionChecker.State

class RequestMultiplePermissions(componentActivity: ComponentActivity) : PermissionChecker {
    private val activityResultLauncher = with(componentActivity) {
        registerForActivityResult(RequestMultiplePermissions()) { result ->
            val m = result.mapValues { (key, value) ->
                if (value) {
                    State.Granted
                } else {
                    State.Denied(shouldShowRequestPermissionRationale(key))
                }
            }

            channel.trySend(Result(m))
        }
    }

    private val channel = Channel<Result>(Int.single)

    suspend fun request(permissions: Set<String>): Result {
        activityResultLauncher.launch(permissions.toTypedArray())

        return channel.receive()
    }
}
