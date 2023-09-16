@file:Suppress("unused")

package wing.tree.bionda.data.extension

import android.icu.util.Calendar
import wing.tree.bionda.data.model.Decorator
import wing.tree.bionda.data.top.level.baseDateFormat
import wing.tree.bionda.data.top.level.baseTimeFormat
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.data.top.level.locdateFormat
import wing.tree.bionda.data.top.level.timeFormat
import wing.tree.bionda.data.top.level.timeRangeFirstFormat
import wing.tree.bionda.data.top.level.timeRangeLastFormat
import wing.tree.bionda.data.top.level.tmFcFormat

val Calendar.baseDate: String get() = baseDateFormat.format(this)
val Calendar.baseTime: String get() = baseTimeFormat.format(this)
val Calendar.fcstDate: String get() = baseDate
val Calendar.locdate: String get() = locdateFormat.format(this)
val Calendar.tmFc: String get() = tmFcFormat.format(this)
val Calendar.timeRange: String get() = buildString {
    append(timeRangeFirstFormat.format(cloneAsCalendar()))
    append(timeRangeLastFormat.format(cloneAsCalendar().delayHourOfDayBy(3))) // TODO make to const. or property
}

var Calendar.date: Int
    get() = get(Calendar.DATE)
    set(value) {
        set(Calendar.DATE, value)
    }

var Calendar.dayOfMonth: Int
    get() = get(Calendar.DAY_OF_MONTH)
    set(value) {
        set(Calendar.DAY_OF_MONTH, value)
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

var Calendar.month: Int
    get() = get(Calendar.MONTH)
    set(value) {
        set(Calendar.MONTH, value)
    }

fun Calendar.advanceHourOfDayBy(hourOfDay: Int) = apply {
    this.hourOfDay -= hourOfDay
}

fun Calendar.clearBelowDate(): Calendar = apply {
    clear(Calendar.MILLISECOND)
    clear(Calendar.SECOND)
    clear(Calendar.MINUTE)
    clear(Calendar.HOUR_OF_DAY)
    clear(Calendar.HOUR)
}

fun Calendar.clearBelowHour(): Calendar = apply {
    clear(Calendar.MILLISECOND)
    clear(Calendar.SECOND)
    clear(Calendar.MINUTE)
}

fun Calendar.cloneAsCalendar(): Calendar = with(clone()) {
    if (this is Calendar) {
        this
    } else {
        koreaCalendar(timeInMillis)
    }
}

fun Calendar.cloneAsBaseCalendar(decorator: Decorator.Calendar) = decorator(cloneAsCalendar())

fun Calendar.delayDateBy(date: Int) = apply {
    this.date += date
}

fun Calendar.delayHourOfDayBy(hourOfDay: Int) = apply {
    this.hourOfDay += hourOfDay
}

fun Calendar.time(): String = timeFormat.format(this)
