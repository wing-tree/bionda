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
import wing.tree.bionda.data.constant.EXTRA_ALARM_ID
import wing.tree.bionda.data.extension.negativeOne
import wing.tree.bionda.data.model.Alarm
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.core.map
import wing.tree.bionda.data.core.onFailure
import wing.tree.bionda.data.core.onSuccess
import wing.tree.bionda.data.provider.LocationProvider
import wing.tree.bionda.data.repository.AlarmRepository
import wing.tree.bionda.data.repository.WeatherRepository
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.mapper.VilageFcstMapper
import wing.tree.bionda.model.VilageFcst
import wing.tree.bionda.permissions.PermissionChecker
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.scheduler.AlarmScheduler
import wing.tree.bionda.service.NotificationChannelProvider.Type.FORECAST
import wing.tree.bionda.service.NotificationChannelProvider.Type.LOCATION
import wing.tree.bionda.service.NotificationFactory.Type
import wing.tree.bionda.template.ContentTextTemplate
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service(), PermissionChecker {
    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var locationProvider: LocationProvider

    @Inject
    lateinit var notificationChannelProvider: NotificationChannelProvider

    @Inject
    lateinit var vilageFcstMapper: VilageFcstMapper

    @Inject
    lateinit var weatherRepository: WeatherRepository

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
            val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, Long.negativeOne)
            val alarm = alarmRepository.get(alarmId) ?: return@launch

            if (alarm.off) {
                alarmScheduler.cancel(alarm)

                return@launch
            }

            alarmScheduler.schedule(alarm)

            if (checkSelfMultiplePermissions(permissions)) {
                startForeground(alarm.notificationId)

                when (val location = locationProvider.getLocation()) {
                    is Complete.Success -> {
                        location.value?.toCoordinate()?.let { (nx, ny) ->
                            weatherRepository.getVilageFcst(
                                nx = nx,
                                ny = ny
                            ).map {
                                vilageFcstMapper.toPresentationModel(it)
                            }.onSuccess { forecast ->
                                stopForeground(STOP_FOREGROUND_REMOVE)

                                alarm.postNotification(forecast)

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

                                notification.post(alarm.notificationId)
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

    private fun Alarm.postNotification(vilageFcst: VilageFcst) {
        val channelId = createNotificationChannel(FORECAST)
        val ptyOrSky = ContentTextTemplate.PtyOrSky(context)
        val type = Type.Alarm(channelId, ptyOrSky(vilageFcst), requestCode)
        val notification = NotificationFactory.create(context, type)

        notification.post(notificationId)
    }

    private fun Notification.post(notificationId: Int) {
        if (checkSelfSinglePermission(POST_NOTIFICATIONS)) {
            notificationManager.notify(notificationId, this)
        }
    }
}
