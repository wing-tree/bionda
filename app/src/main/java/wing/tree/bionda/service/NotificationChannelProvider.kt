package wing.tree.bionda.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import wing.tree.bionda.R
import wing.tree.bionda.data.constant.DOT

class NotificationChannelProvider(private val context: Context) {
    enum class Type {
        FORECAST,
        LOCATION
    }

    fun getNotificationChannel(type: Type): NotificationChannel = with(context) {
        when (type) {
            Type.FORECAST -> {
                val channelId = packageName.plus("$DOT${Type.FORECAST.name}")
                val channelName = getString(R.string.app_name).plus("$DOT${Type.FORECAST.name}")
                val importance = NotificationManager.IMPORTANCE_DEFAULT

                NotificationChannel(channelId, channelName, importance).apply {
                    setShowBadge(true)
                }
            }

            Type.LOCATION -> {
                val channelId = packageName.plus("$DOT${Type.LOCATION.name}")
                val channelName = getString(R.string.app_name).plus("$DOT${Type.LOCATION.name}")
                val importance = NotificationManager.IMPORTANCE_MIN

                NotificationChannel(channelId, channelName, importance).apply {
                    setShowBadge(false)
                }
            }
        }
    }
}
