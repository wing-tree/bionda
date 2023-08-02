package wing.tree.bionda.receiver

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import wing.tree.bionda.R
import wing.tree.bionda.constant.EXTRA_NOTIFICATION_ID
import wing.tree.bionda.data.extension.NEGATIVE_ONE
import wing.tree.bionda.data.extension.ONE_SECOND
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.forecast.Item
import wing.tree.bionda.data.model.onFailure
import wing.tree.bionda.data.model.onSuccess
import wing.tree.bionda.data.repository.ForecastRepository
import wing.tree.bionda.data.repository.NoticeRepository
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.permissions.MultiplePermissionsChecker
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.provider.LocationProvider
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

    @Inject
    lateinit var locationProvider: LocationProvider

    @Inject
    lateinit var noticeRepository: NoticeRepository

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val permissions = locationPermissions.toTypedArray()

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        coroutineScope.launch {
            val notificationId = intent.getLongExtra(EXTRA_NOTIFICATION_ID, Long.NEGATIVE_ONE)
            val notice = noticeRepository.get(notificationId) ?: return@launch

            if (notice.checked.not()) {
                return@launch
            }

            launch {
                delay(Long.ONE_SECOND)

                alarmScheduler.schedule(notice)
            }

            if (context.checkSelfPermission(*permissions)) {
                when (val location = locationProvider.getLocation()) {
                    is Complete.Success -> {
                        val (nx, ny) = location.data?.toCoordinate() ?: return@launch

                        forecastRepository.getVilageFcst(
                            nx = nx,
                            ny = ny
                        ).onSuccess { forecast ->
                            context.postNotification(
                                items = forecast.pty,
                                notice = notice
                            )
                        }.onFailure {
                            Timber.e(it)

                            context.postNotification(
                                items = emptyList(),
                                notice = notice
                            )
                        }
                    }

                    else -> {
                        if (location is Complete.Failure) {
                            Timber.e(location.throwable)
                        }
                    }
                }
            }
        }
    }

    private fun Context.postNotification(items: List<Item>, notice: Notice) {
        val notificationId = notice.notificationId.int
        val notificationManager = NotificationManagerCompat.from(this)

        val channelId = packageName
        val channelName = getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(channelId, channelName, importance).apply {
            setShowBadge(true)
        }

        notificationManager.createNotificationChannel(notificationChannel)

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
            .setShowWhen(true)
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
