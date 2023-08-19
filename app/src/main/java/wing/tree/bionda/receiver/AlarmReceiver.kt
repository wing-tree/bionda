package wing.tree.bionda.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wing.tree.bionda.data.constant.EXTRA_ALARM_ID
import wing.tree.bionda.data.extension.negativeOne
import wing.tree.bionda.permissions.PermissionChecker
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.service.AlarmService

class AlarmReceiver : BroadcastReceiver(), PermissionChecker {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val permissions = locationPermissions.toTypedArray()

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        coroutineScope.launch {
            val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, Long.negativeOne)

            if (context.checkSelfMultiplePermissions(permissions)) {
                context.startForegroundService(
                    Intent(context, AlarmService::class.java).apply {
                        putExtra(EXTRA_ALARM_ID, alarmId)
                    }
                )
            }
        }
    }
}
