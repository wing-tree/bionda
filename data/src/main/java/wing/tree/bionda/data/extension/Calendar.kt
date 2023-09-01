package wing.tree.bionda.data.extension

import android.icu.util.Calendar
import wing.tree.bionda.data.model.Decorator
import wing.tree.bionda.data.top.level.baseDateFormat
import wing.tree.bionda.data.top.level.baseTimeFormat
import wing.tree.bionda.data.top.level.koreaCalendarOf
import wing.tree.bionda.data.top.level.locdateFormat
import wing.tree.bionda.data.top.level.tmFcFormat
import wing.tree.bionda.data.top.level.uvIdxTimeFormat

val Calendar.baseDate: String get() = baseDateFormat.format(this)
val Calendar.baseTime: String get() = baseTimeFormat.format(this)
val Calendar.locdate: String get() = locdateFormat.format(this)
val Calendar.tmFc: String get() = tmFcFormat.format(this)
val Calendar.uvIdxTime: String get() = uvIdxTimeFormat.format(this)

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

fun Calendar.advanceHourOfDayBy(hourOfDay: Int) = apply {
    this.hourOfDay -= hourOfDay
}

fun Calendar.cloneAsCalendar(): Calendar = with(clone()) {
    if (this is Calendar) {
        this
    } else {
        koreaCalendarOf(timeInMillis)
    }
}

fun Calendar.cloneAsBaseCalendar(decorator: Decorator.Calendar) = decorator(cloneAsCalendar())
