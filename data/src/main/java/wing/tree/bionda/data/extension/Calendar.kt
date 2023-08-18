package wing.tree.bionda.data.extension

import android.icu.util.Calendar
import wing.tree.bionda.data.regular.calendarOf
import wing.tree.bionda.data.top.level.baseDateFormat
import wing.tree.bionda.data.top.level.baseTimeFormat

val Calendar.baseDate: String get() = baseDateFormat.format(time)
val Calendar.baseTime: String get() = baseTimeFormat.format(time)

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

fun Calendar.cloneAsApiAvailabilityCalendar() = cloneAsCalendar().apply {
    hourOfDay = when (hourOfDay) {
        in 2 until 5 -> 2
        in 5 until 8 -> 5
        in 8 until 11 -> 8
        in 11 until 14 -> 11
        in 14 until 17 -> 14
        in 17 until 20 -> 17
        in 20 until 23 -> 20
        else -> {
            date -= Int.one
            23
        }
    }

    minute = Int.ten
}

fun Calendar.cloneAsBaseCalendar() = cloneAsCalendar().apply {
    if (this < cloneAsApiAvailabilityCalendar()) {
        hourOfDay -= 3
    }

    hourOfDay = when (hourOfDay) {
        in 2 until 5 -> 2
        in 5 until 8 -> 5
        in 8 until 11 -> 8
        in 11 until 14 -> 11
        in 14 until 17 -> 14
        in 17 until 20 -> 17
        in 20 until 23 -> 20
        else -> {
            date -= Int.one
            23
        }
    }

    minute = Int.zero
}
