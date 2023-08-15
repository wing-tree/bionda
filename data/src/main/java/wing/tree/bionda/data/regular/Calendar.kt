package wing.tree.bionda.data.regular

import android.icu.util.Calendar
import wing.tree.bionda.data.extension.date
import wing.tree.bionda.data.extension.halfAnHour
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.model.DetailedFunction
import java.util.Locale

fun baseCalendar(
    detailFunction: DetailedFunction
): Calendar = koreaCalendar().apply {
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

fun calendarOf(
    timeInMillis: Long? = null
): Calendar = Calendar.getInstance().apply {
    timeInMillis?.let {
        this.timeInMillis = it
    }
}

fun fcstCalendar(hourOfDay: Int): Calendar = koreaCalendar().apply {
    this.hourOfDay = hourOfDay
}

fun koreaCalendar(
    hourOfDay: Int? = null,
    minute: Int? = null
): Calendar = Calendar.getInstance(Locale.KOREA).apply {
    hourOfDay?.let {
        this.hourOfDay = it
    }

    minute?.let {
        this.minute = it
    }
}
