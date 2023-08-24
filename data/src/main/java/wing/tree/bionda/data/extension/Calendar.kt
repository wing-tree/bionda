package wing.tree.bionda.data.extension

import android.icu.util.Calendar
import wing.tree.bionda.data.regular.koreaCalendarOf
import wing.tree.bionda.data.top.level.baseDateFormat
import wing.tree.bionda.data.top.level.baseTimeFormat

val Calendar.baseDate: String get() = baseDateFormat.format(time)
val Calendar.baseTime: String get() = baseTimeFormat.format(time)

var Calendar.date: Int
    get() = get(Calendar.DATE)
    set(value) {
        set(Calendar.DATE, value)
    }

var Calendar.dayOfWeek: Int
    get() = get(Calendar.DAY_OF_WEEK)
    set(value) {
        set(Calendar.DAY_OF_WEEK, value)
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

var Calendar.julianDay: Int
    get() = get(Calendar.JULIAN_DAY)
    set(value) {
        set(Calendar.JULIAN_DAY, value)
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
        koreaCalendarOf(timeInMillis)
    }
}

fun Calendar.cloneAsApiAvailabilityCalendar() = cloneAsCalendar().apply {
    hourOfDay = if (hourOfDay < 2) {
        date -= Int.one; 23
    } else {
        with(hourOfDay.inc().div(3)) {
            times(3).dec()
        }
    }

    minute = Int.ten
}

fun Calendar.cloneAsBaseCalendar() = cloneAsCalendar().apply {
    if (this < cloneAsApiAvailabilityCalendar()) {
        hourOfDay -= 3
    }

    hourOfDay = if (hourOfDay < 2) {
        date -= Int.one; 23
    } else {
        with(hourOfDay.inc().div(3)) {
            times(3).dec()
        }
    }

    minute = Int.zero
}
