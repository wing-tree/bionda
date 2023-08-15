package wing.tree.bionda.view.compose.composable

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

    Column(modifier = modifier.padding(16.dp)) {
        permissions.forEach { permission ->
            permissionRational[permission]?.let { stringRes ->
                OutlinedCard(
                    onClick = {
                        when (permission) {
                            ACCESS_BACKGROUND_LOCATION -> onClick(Action.ACCESS_BACKGROUND_LOCATION)
                            POST_NOTIFICATIONS -> onClick(Action.POST_NOTIFICATIONS)
                        }
                    }
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text(
                            text = stringResource(id = stringRes),
                            style = typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
