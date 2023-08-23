package wing.tree.bionda.view.compose.composable.alarm

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.SCHEDULE_EXACT_ALARM
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
import kotlinx.collections.immutable.ImmutableSet
import wing.tree.bionda.permissions.permissionRational
import wing.tree.bionda.view.state.AlarmState.Action.RequestPermissions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestPermissions(
    requestPermissions: ImmutableSet<String>,
    onAction: (RequestPermissions) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        requestPermissions.forEach { permission ->
            permissionRational[permission]?.let { stringRes ->
                OutlinedCard(
                    onClick = {
                        when (permission) {
                            ACCESS_BACKGROUND_LOCATION -> onAction(RequestPermissions.ACCESS_BACKGROUND_LOCATION)
                            POST_NOTIFICATIONS -> onAction(RequestPermissions.POST_NOTIFICATIONS)
                            SCHEDULE_EXACT_ALARM -> onAction(RequestPermissions.SCHEDULE_EXACT_ALARM)
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
