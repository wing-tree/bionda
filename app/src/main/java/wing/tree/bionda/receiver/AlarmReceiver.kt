package wing.tree.bionda.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wing.tree.bionda.data.extension.negativeOne
import wing.tree.bionda.permissions.MultiplePermissionsChecker
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.service.NoticeService

class AlarmReceiver : BroadcastReceiver(), MultiplePermissionsChecker {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val permissions = locationPermissions.toTypedArray()

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        coroutineScope.launch {
            val notificationId = intent.getLongExtra(EXTRA_NOTIFICATION_ID, Long.negativeOne)

            if (context.checkSelfPermission(*permissions)) {
                context.startForegroundService(
                    Intent(context, NoticeService::class.java).apply {
                        putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                    }
                )
            }
        }
    }
}
