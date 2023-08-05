package wing.tree.bionda.permissions

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.os.Build
import wing.tree.bionda.R

val permissionRational: Map<String, Int> = buildMap {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        set(ACCESS_BACKGROUND_LOCATION, R.string.access_background_location_permission_rationale)
    }
}
