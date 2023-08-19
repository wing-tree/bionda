package wing.tree.bionda.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.app.NotificationCompat
import wing.tree.bionda.R
import wing.tree.bionda.data.constant.SCHEME_PACKAGE
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.view.MainActivity

object NotificationFactory {
    sealed interface Type {
        val channelId: String

        data class AccessBackgroundLocation(override val channelId: String) : Type
        data class Alarm(
            override val channelId: String,
            val contentText: String,
            val requestCode: Int
        ) : Type

        data class Location(override val channelId: String) : Type
    }

    fun create(context: Context, type: Type): Notification = when (type) {
        is Type.Alarm -> type.create(context)
        is Type.AccessBackgroundLocation -> type.create(context)
        is Type.Location -> type.create(context)
    }

    private fun Type.AccessBackgroundLocation.create(context: Context): Notification {
        val pendingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts(
                SCHEME_PACKAGE,
                context.packageName,
                null
            )

            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }.let {
            PendingIntent.getActivity(
                context,
                Int.zero,
                it,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        return NotificationCompat.Builder(context, channelId)
            .setShowWhen(true)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("위치 권한 요청")
            .setContentText(context.getString(R.string.access_background_location_permission_rationale))
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .build()
    }

    private fun Type.Alarm.create(context: Context): Notification {
        val contentTitle = context.getString(R.string.take_an_umbrella)
        val style = NotificationCompat.BigTextStyle().bigText(contentText)

        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, channelId)
            .setShowWhen(true)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .setStyle(style)
            .build()
    }

    private fun Type.Location.create(context: Context): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setShowWhen(true)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("위치 정보 가져오는 중.")
            .setContentText("정확한 위치를 가져옵니다.")
            .build()
    }
}
