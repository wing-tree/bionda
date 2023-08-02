package wing.tree.bionda.view.compose.composable

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wing.tree.bionda.data.extension.containsAny
import wing.tree.bionda.view.state.RequestPermissionsState

@Composable
fun RequestPermissions(
    state: RequestPermissionsState,
    modifier: Modifier = Modifier
) {
    val permissions = state.permissions

    Column(modifier = modifier) {
        if (permissions.containsAny(listOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION))) {
            Text(text = "${listOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)}")
        }

        permissions.forEach { permission ->
            when (permission) {
                ACCESS_BACKGROUND_LOCATION -> Text(text = permission)
                POST_NOTIFICATIONS -> Text(text = permission)
            }
        }
    }
}
