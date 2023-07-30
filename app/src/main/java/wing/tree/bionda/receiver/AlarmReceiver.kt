package wing.tree.bionda.receiver

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.IntentCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import wing.tree.bionda.LocationProvider
import wing.tree.bionda.R
import wing.tree.bionda.constant.EXTRA_NOTICE
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.baseTime
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.model.Result
import wing.tree.bionda.data.model.forecast.Item
import wing.tree.bionda.data.model.onFailure
import wing.tree.bionda.data.model.onSuccess
import wing.tree.bionda.data.regular.baseCalendar
import wing.tree.bionda.data.repository.ForecastRepository
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.model.Coordinate
import wing.tree.bionda.permissions.MultiplePermissionsChecker
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.view.MainActivity
import java.time.LocalTime.NOON
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver(), MultiplePermissionsChecker {
    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var forecastRepository: ForecastRepository

    private val permissions = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
    private val supervisorScope = CoroutineScope(Dispatchers.IO.plus(SupervisorJob()))

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        val notice = IntentCompat.getParcelableExtra(
            intent,
            EXTRA_NOTICE,
            Notice::class.java
        ) ?: return

        supervisorScope.launch {
            if (context.checkSelfPermission(*permissions)) {
                when (val location = LocationProvider(context).getLocation()) {
                    is Result.Complete.Success -> {
                        val baseCalendar = baseCalendar()
                        val (nx, ny) = location.data?.toCoordinate() ?: Coordinate.seoul

                        forecastRepository.getUltraSrtFcst(
                            baseDate = baseCalendar.baseDate,
                            baseTime = baseCalendar.baseTime,
                            nx = nx,
                            ny = ny
                        ).onSuccess { forecast ->
                            context.postNotification(
                                items = forecast.pty,
                                notice = notice
                            )
                        }.onFailure {
                            // TODO Log/report
                            context.postNotification(
                                items = emptyList(),
                                notice = notice
                            )
                        }
                    }
                    else -> {
                        // TODO Log/report
                    }
                }
            }
        }
    }

    private fun Context.postNotification(items: List<Item>, notice: Notice) {
        val notificationManager = NotificationManagerCompat.from(this)

        val channelId = packageName
        val channelName = getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(channelId, channelName, importance).apply {
            setShowBadge(true)
        }

        notificationManager.createNotificationChannel(notificationChannel)

        val notificationId = notice.notificationId

        val contentTitle = getString(R.string.take_an_umbrella)
        val contentText = items.toContentText()

        val pendingIntent = PendingIntent.getActivity(
            this,
            notice.requestCode,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setShowWhen(false)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (checkSelfPermission(*arrayOf(POST_NOTIFICATIONS))) {
            notificationManager.notify(notificationId, notification)
        }
    }

    private fun List<Item>.toContentText(): String {
        val list =  groupBy {
            it.fcstValue
        }
            .toList()

        return list.mapIndexed { index, (key, value) ->
            val am = mutableListOf<Int>()
            val pm = mutableListOf<Int>()

            value.forEach { item ->
                if (NOON.hour > item.fcstTime) {
                    am.add(item.fcstTime)
                } else {
                    pm.add(item.fcstTime)
                }
            }

            buildString {
                if (am.isNotEmpty()) {
                    append("오전 ")
                    append(am.joinToString(separator = ",", postfix = "시"))
                }

                if (pm.isNotEmpty()) {
                    append("오후 ")
                    append(pm.joinToString(separator = ",", postfix = "시"))
                }

                append("에는 $key")

                if (index == list.lastIndex) {
                    append("가/이 올 예정입니다.")
                } else {
                    append(",")
                }
            }
        }.joinToString(separator = "\n")
    }
}
