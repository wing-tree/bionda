package wing.tree.bionda.service

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import wing.tree.bionda.data.constant.EXTRA_NOTICE_ID
import wing.tree.bionda.data.extension.negativeOne
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.map
import wing.tree.bionda.data.model.onFailure
import wing.tree.bionda.data.model.onSuccess
import wing.tree.bionda.data.provider.LocationProvider
import wing.tree.bionda.data.repository.ForecastRepository
import wing.tree.bionda.data.repository.NoticeRepository
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.permissions.PermissionChecker
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.service.NotificationChannelProvider.Type.FORECAST
import wing.tree.bionda.service.NotificationChannelProvider.Type.LOCATION
import wing.tree.bionda.service.NotificationFactory.Type
import wing.tree.bionda.template.ContentTextTemplate
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

    @Inject
    lateinit var notificationChannelProvider: NotificationChannelProvider

    private val context = this
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

            if (notice.off) {
                alarmScheduler.cancel(notice)

                return@launch
            }

            alarmScheduler.schedule(notice)

            if (checkSelfMultiplePermissions(permissions)) {
                startForeground(notice.notificationId)

                when (val location = locationProvider.getLocation()) {
                    is Complete.Success -> {
                        location.data?.toCoordinate()?.let { (nx, ny) ->
                            forecastRepository.get(
                                nx = nx,
                                ny = ny
                            ).map {
                                Forecast.toPresentationModel(it)
                            }.onSuccess { forecast ->
                                stopForeground(STOP_FOREGROUND_REMOVE)

                                notice.postNotification(forecast)

                                stopSelf()
                            }.onFailure {
                                Timber.e(it)
                                stopSelf()
                            }
                        } ?: run {
                            if (checkSelfSinglePermission(ACCESS_BACKGROUND_LOCATION).not()) {
                                val channelId = createNotificationChannel(FORECAST)
                                val notification = NotificationFactory.create(
                                    context,
                                    Type.AccessBackgroundLocation(channelId)
                                )

                                stopForeground(STOP_FOREGROUND_REMOVE)

                                notification.post(notice.notificationId)
                            }

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

    private fun createNotificationChannel(
        type: NotificationChannelProvider.Type
    ) = notificationChannelProvider.getNotificationChannel(type).also {
        notificationManager.createNotificationChannel(it)
    }
        .id

    private fun startForeground(notificationId: Int) {
        val channelId = createNotificationChannel(LOCATION)
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

    private fun Notice.postNotification(forecast: Forecast) {
        val channelId = createNotificationChannel(FORECAST)
        val ptyOrSky = ContentTextTemplate.PtyOrSky(context)
        val type = Type.Notice(channelId, ptyOrSky(forecast), requestCode)
        val notification = NotificationFactory.create(context, type)

        notification.post(notificationId)
    }

    private fun Notification.post(notificationId: Int) {
        if (checkSelfSinglePermission(POST_NOTIFICATIONS)) {
            notificationManager.notify(notificationId, this)
        }
    }
}
