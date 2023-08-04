package wing.tree.bionda.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.icu.text.DateFormatSymbols
import android.icu.util.Calendar
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import wing.tree.bionda.R
import wing.tree.bionda.data.extension.comma
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.negativeOne
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.forecast.Item
import wing.tree.bionda.data.model.onFailure
import wing.tree.bionda.data.model.onSuccess
import wing.tree.bionda.data.provider.LocationProvider
import wing.tree.bionda.data.repository.ForecastRepository
import wing.tree.bionda.data.repository.NoticeRepository
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.permissions.MultiplePermissionsChecker
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.view.MainActivity
import java.time.LocalTime
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class NoticeService : Service(), MultiplePermissionsChecker {
    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var forecastRepository: ForecastRepository

    @Inject
    lateinit var locationProvider: LocationProvider

    @Inject
    lateinit var noticeRepository: NoticeRepository

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val notificationManager by lazy {
        NotificationManagerCompat.from(this)
    }

    private val permissions = locationPermissions.toTypedArray()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent ?: return START_NOT_STICKY

        coroutineScope.launch {
            val notificationId = intent.getLongExtra(EXTRA_NOTIFICATION_ID, Long.negativeOne)
            val notice = noticeRepository.get(notificationId) ?: return@launch

            if (notice.checked.not()) {
                alarmScheduler.cancel(notice)

                return@launch
            }

            alarmScheduler.schedule(notice)

            if (checkSelfPermission(*permissions)) {
                startForeground(notice)

                when (val location = locationProvider.getLocation()) {
                    is Complete.Success -> {
                        location.data?.toCoordinate()?.let { (nx, ny) ->
                            forecastRepository.getVilageFcst(
                                nx = nx,
                                ny = ny
                            ).onSuccess { forecast ->
                                stopForeground(STOP_FOREGROUND_REMOVE)
                                postNotification(
                                    items = forecast.pty,
                                    notice = notice
                                )

                                stopSelf()
                            }.onFailure {
                                stopSelf()
                            }
                        } ?: stopSelf()
                    }

                    else -> {
                        if (location is Complete.Failure) {
                            Timber.e(location.throwable)
                        }

                        stopSelf()
                    }
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun createLocationChannel(): String {
        val channelId = packageName + "location"
        val channelName = getString(R.string.app_name) + "location"
        val importance = NotificationManager.IMPORTANCE_MIN
        val locationChannel = NotificationChannel(channelId, channelName, importance).apply {
            setShowBadge(false)
        }

        notificationManager.createNotificationChannel(locationChannel)

        return channelId
    }

    private fun startForeground(notice: Notice) {
        val channelId = createLocationChannel()
        val notificationId = notice.notificationId.int.inc()
        val notification = NotificationCompat.Builder(this, channelId)
            .setShowWhen(true)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("위치 정보 가져오는 중.")
            .setContentText("정확한 위치를 가져옵니다.")
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(notificationId, notification)
        }
    }

    private fun postNotification(items: List<Item>, notice: Notice) {
        val notificationId = notice.notificationId.int.inc()

        val channelId = packageName
        val channelName = getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(channelId, channelName, importance).apply {
            setShowBadge(true)
        }

        notificationManager.createNotificationChannel(notificationChannel)

        val contentTitle = getString(R.string.take_an_umbrella)
        val contentText = items.toContentText()
        val style = NotificationCompat.BigTextStyle().bigText(contentText)

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
            .setStyle(style)
            .build()

        if (checkSelfPermission(*arrayOf(Manifest.permission.POST_NOTIFICATIONS))) {
            notificationManager.notify(notificationId, notification)
        }
    }

    private fun List<Item>.toContentText(): String {
        val amPmStrings = DateFormatSymbols(Locale.KOREA).amPmStrings
        val amString = amPmStrings[Calendar.AM]
        val pmString = amPmStrings[Calendar.PM]

        val list =  groupBy {
            it.fcstValue
        }
            .toList()

        return list.mapIndexed { index, (key, value) ->
            val am = mutableListOf<Int>()
            val pm = mutableListOf<Int>()

            value.forEach { item ->
                if (LocalTime.NOON.hour > item.fcstTime) {
                    am.add(item.fcstTime)
                } else {
                    pm.add(item.fcstTime)
                }
            }

            buildString {
                if (am.isNotEmpty()) {
                    append("$amString ")
                    append(am.joinToString(separator = String.comma, postfix = "시"))
                }

                if (pm.isNotEmpty()) {
                    append("$pmString ")
                    append(pm.joinToString(separator = String.comma, postfix = "시"))
                }

                append("에는 $key")

                if (index == list.lastIndex) {
                    append("가/이 올 예정입니다.")
                } else {
                    append(String.comma)
                }
            }
        }
            .joinToString(separator = "\n")
    }
}
