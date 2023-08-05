package wing.tree.bionda.view.compose.composable

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import wing.tree.bionda.permissions.permissionRational
import wing.tree.bionda.view.state.MainState.Action
import wing.tree.bionda.view.state.RequestPermissionsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestPermissions(
    state: RequestPermissionsState,
    onClick: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    val permissions = state.permissions

    Column(modifier = modifier) {
        permissions.forEach { permission ->
            permissionRational[permission]?.let { stringRes ->
                ElevatedCard(
                    onClick = {
                        when (permission) {
                            ACCESS_BACKGROUND_LOCATION -> onClick(Action.ACCESS_BACKGROUND_LOCATION)
                            POST_NOTIFICATIONS -> onClick(Action.POST_NOTIFICATIONS)
                        }
                    }
                ) {
                    Text(text = stringResource(id = stringRes))
                }
            }
        }
    }
}
