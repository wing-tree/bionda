package wing.tree.bionda.permissions

import android.Manifest
import kotlinx.collections.immutable.persistentListOf

val locationPermissions = persistentListOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
)
