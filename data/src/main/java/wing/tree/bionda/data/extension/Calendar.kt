package wing.tree.bionda.data.extension

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import wing.tree.bionda.data.model.DetailedFunction
import wing.tree.bionda.data.regular.calendarOf
import java.util.Locale

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

fun Calendar.cloneAsApiDeliveryCalendar(detailFunction: DetailedFunction) = cloneAsCalendar().apply {
    when (detailFunction) {
        DetailedFunction.ULTRA_SRT_FCST -> {
            minute = when (minute) {
                in 5 until 15 -> 5
                in 15 until 25 -> 15
                in 25 until 35 -> 25
                in 35 until 45 -> 35
                in 45 until 55 -> 45
                in 55 until 60 -> 55
                else -> {
                    hourOfDay -= Int.one
                    55
                }
            }
        }

        DetailedFunction.VILAGE_FCST -> {
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
    }
}

fun Calendar.cloneAsBaseCalendar(detailFunction: DetailedFunction) = cloneAsCalendar().apply {
    when (detailFunction) {
        DetailedFunction.ULTRA_SRT_FCST -> {
            if (minute < Int.halfAnHour) {
                hourOfDay -= Int.one
            }

            minute = Int.halfAnHour
        }

        DetailedFunction.VILAGE_FCST -> {
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
    }
}
