package wing.tree.bionda.view.compose.composable

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wing.tree.bionda.view.state.MainState.Action
import wing.tree.bionda.view.state.RequestPermissionsState

@Composable
fun RequestPermissions(
    state: RequestPermissionsState,
    onClick: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    val permissions = state.permissions

    Column(modifier = modifier) {
        permissions.forEach { permission ->
            when (permission) {
                ACCESS_BACKGROUND_LOCATION -> Text(
                    text = permission,
                    modifier = Modifier.clickable {
                        onClick(Action.ACCESS_BACKGROUND_LOCATION)
                    }
                )
                POST_NOTIFICATIONS -> Text(
                    text = permission,
                    modifier = Modifier.clickable {
                        onClick(Action.POST_NOTIFICATIONS)
                    }
                )
            }
        }
    }
}
