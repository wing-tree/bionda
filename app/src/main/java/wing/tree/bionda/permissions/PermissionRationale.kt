package wing.tree.bionda.permissions

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.SCHEDULE_EXACT_ALARM
import android.os.Build
import wing.tree.bionda.R

val permissionRational: Map<String, Int> = buildMap {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        set(ACCESS_BACKGROUND_LOCATION, R.string.access_background_location_permission_rationale)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        set(SCHEDULE_EXACT_ALARM, R.string.schedule_exact_alarm_permission_rationale)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        set(POST_NOTIFICATIONS, R.string.post_notification_permission_rationale)
    }
}
