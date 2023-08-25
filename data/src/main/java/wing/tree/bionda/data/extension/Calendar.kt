package wing.tree.bionda.data.extension

import android.icu.util.Calendar
import wing.tree.bionda.data.model.CalendarDecorator
import wing.tree.bionda.data.top.level.baseDateFormat
import wing.tree.bionda.data.top.level.baseTimeFormat
import wing.tree.bionda.data.top.level.koreaCalendarOf
import wing.tree.bionda.data.top.level.tmFcFormat

val Calendar.baseDate: String get() = baseDateFormat.format(time)
val Calendar.baseTime: String get() = baseTimeFormat.format(time)
val Calendar.tmFc: String get() = tmFcFormat.format(time)

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

fun Calendar.cloneAsBaseCalendar(base: CalendarDecorator.Base) = base(cloneAsCalendar())

fun Calendar.advanceHourOfDayBy(hourOfDay: Int) = apply {
    this.hourOfDay -= hourOfDay
}
