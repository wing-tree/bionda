package wing.tree.bionda.permissions

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.channels.Channel
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.permissions.PermissionChecker.Result
import wing.tree.bionda.permissions.PermissionChecker.State

class RequestMultiplePermissions : PermissionChecker {
    private val contract = ActivityResultContracts.RequestMultiplePermissions()
    private var activityResultLauncher: ActivityResultLauncher<Array<String>>? = null
    private var channel: Channel<Result>? = null

    fun initialize(componentActivity: ComponentActivity) = with(componentActivity) {
        activityResultLauncher = registerForActivityResult(contract) { result ->
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

    suspend fun request(permissions: Set<String>): Result {
        val activityResultLauncher = activityResultLauncher

        checkNotNull(activityResultLauncher)

        return with(Channel<Result>(Int.one)) {
            channel = this

            activityResultLauncher.launch(permissions.toTypedArray())
            receive()
        }
    }
}
