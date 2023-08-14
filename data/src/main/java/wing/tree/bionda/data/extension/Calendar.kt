package wing.tree.bionda.data.extension

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import wing.tree.bionda.data.regular.calendarOf
import java.util.Locale

val Calendar.apiDeliveryDate: String get() = baseDate
val Calendar.apiDeliveryTime: String get() = baseTime

val Calendar.baseDate: String get() = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
    .format(time)

val Calendar.baseTime: String get() = SimpleDateFormat("HHmm", Locale.KOREA)
    .format(time)

var Calendar.date: Int
    get() = get(Calendar.DATE)
    set(value) {
        set(Calendar.DATE, value)
    }

var Calendar.hour: Int
    get() = get(Calendar.HOUR)
    set(value) {
        set(Calendar.HOUR, value)
    }

var Calendar.hourOfDay: Int
    get() = get(Calendar.HOUR_OF_DAY)
    set(value) {
        set(Calendar.HOUR_OF_DAY, value)
    }

var Calendar.minute: Int
    get() = get(Calendar.MINUTE)
    set(value) {
        set(Calendar.MINUTE, value)
    }

fun Calendar.cloneAsCalendar(): Calendar = with(clone()) {
    if (this is Calendar) {
        this
    } else {
        calendarOf(timeInMillis)
    }
}
