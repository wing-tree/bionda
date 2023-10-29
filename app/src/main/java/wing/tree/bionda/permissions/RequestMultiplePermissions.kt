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
            result.mapValues { (key, value) ->
                if (value) {
                    State.Granted
                } else {
                    State.Denied(shouldShowRequestPermissionRationale(key))
                }
            }.let {
                with(channel) {
                    checkNotNull(this)
                    trySend(Result(it))
                }
            }
        }
    }

    private var channel: Channel<Result>? = null

    suspend fun request(permissions: Set<String>): Result {
        return with(Channel<Result>(Int.single)) {
            channel = this

            activityResultLauncher.launch(permissions.toTypedArray())
            receive()
        }
    }
}
