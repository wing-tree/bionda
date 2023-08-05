package wing.tree.bionda.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import wing.tree.bionda.data.constant.EXTRA_NOTICE_ID
import wing.tree.bionda.data.extension.date
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.regular.koreaCalendar
import wing.tree.bionda.receiver.AlarmReceiver

class AlarmScheduler(private val context: Context) {
    private val alarmManager by lazy {
        context.getSystemService(AlarmManager::class.java)
    }

    fun schedule(notice: Notice) {
        val pendingIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_NOTICE_ID, notice.id)
        }
            .let {
                PendingIntent.getBroadcast(
                    context,
                    notice.requestCode,
                    it,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        val triggerAtMillis = koreaCalendar().apply {
            clear(Calendar.SECOND)
            clear(Calendar.MILLISECOND)

            hourOfDay = notice.hour
            minute = notice.minute

            if (timeInMillis < System.currentTimeMillis()) {
                date += Int.one
            }
        }
            .timeInMillis

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun cancel(notice: Notice) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notice.requestCode,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }
}
