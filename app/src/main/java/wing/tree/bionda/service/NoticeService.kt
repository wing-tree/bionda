package wing.tree.bionda.service

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.icu.text.DateFormatSymbols
import android.icu.util.Calendar
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import wing.tree.bionda.R
import wing.tree.bionda.data.constant.EXTRA_NOTICE_ID
import wing.tree.bionda.data.extension.comma
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
import wing.tree.bionda.permissions.PermissionChecker
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.service.NotificationFactory.Type
import java.time.LocalTime
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class NoticeService : Service(), PermissionChecker {
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

        val context = this

        coroutineScope.launch {
            val noticeId = intent.getLongExtra(EXTRA_NOTICE_ID, Long.negativeOne)
            val notice = noticeRepository.get(noticeId) ?: return@launch

            if (notice.checked.not()) {
                alarmScheduler.cancel(notice)

                return@launch
            }

            alarmScheduler.schedule(notice)

            if (checkSelfMultiplePermissions(permissions)) {
                startForeground(notice.notificationId)

                if (checkSelfSinglePermission(ACCESS_BACKGROUND_LOCATION).not()) {
                    val notification = NotificationFactory.create(
                        context,
                        Type.AccessBackgroundLocation(packageName) // todo content channel id. - forecast channel.
                    )

                    stopForeground(STOP_FOREGROUND_REMOVE)
                    postNotification(notice.notificationId, notification)

                    stopSelf()

                    return@launch
                }

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
                        } ?: run {
                            stopSelf()
                        }
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

    private fun startForeground(notificationId: Int) {
        val channelId = createLocationChannel()
        val notification = NotificationFactory.create(
            this,
            Type.Location(channelId)
        )

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

    private fun postNotification(notificationId: Int, notification: Notification) {
        if (checkSelfSinglePermission(POST_NOTIFICATIONS)) {
            notificationManager.notify(notificationId, notification)
        }
    }

    private fun postNotification(items: List<Item>, notice: Notice) {
        val notificationId = notice.notificationId

        val channelId = packageName
        val channelName = getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(channelId, channelName, importance).apply {
            setShowBadge(true)
        }

        notificationManager.createNotificationChannel(notificationChannel)

        val type = Type.Notice(channelId, items.toContentText(), notice.requestCode)
        val notification = NotificationFactory.create(this, type)

        postNotification(notificationId, notification)
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
