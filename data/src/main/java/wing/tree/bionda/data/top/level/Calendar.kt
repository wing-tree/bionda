package wing.tree.bionda.data.top.level

import android.icu.util.Calendar
import wing.tree.bionda.data.extension.clearBelowDate
import wing.tree.bionda.data.extension.cloneAsBaseCalendar
import wing.tree.bionda.data.extension.date
import wing.tree.bionda.data.extension.dayOfMonth
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.data.extension.month
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.model.Decorator
import java.util.Date
import java.util.Locale

val koreaCalendar: Calendar get() = koreaCalendar()

fun baseCalendar(decorator: Decorator.Calendar): Calendar = koreaCalendar.cloneAsBaseCalendar(decorator)
fun fcstCalendar(hourOfDay: Int): Calendar = koreaCalendar.apply {
    this.hourOfDay = hourOfDay
}

fun koreaCalendar(block: Calendar.() -> Unit) = koreaCalendar.apply(block)
fun koreaCalendar(date: Date): Calendar = koreaCalendar.apply {
    time = date
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

fun koreaCalendar(baseDate: String, baseTime: String): Calendar = koreaCalendar(
    baseDateFormat.parse(baseDate)
).also {
    with(koreaCalendar(baseTimeFormat.parse(baseTime))) {
        it.hourOfDay = hourOfDay
        it.minute = minute
    }
}

fun koreaCalendar(timeInMillis: Long): Calendar = koreaCalendar.also {
    it.timeInMillis = timeInMillis
}

fun may(dayOfMonth: Calendar.() -> Int) = koreaCalendar.clearBelowDate().also {
    it.month = Calendar.MAY
    it.dayOfMonth = dayOfMonth(it)
}

fun september(dayOfMonth: Calendar.() -> Int) = koreaCalendar.clearBelowDate().also {
    it.month = Calendar.SEPTEMBER
    it.dayOfMonth = dayOfMonth(it)
}

fun tmFcCalendar() = koreaCalendar(minute = Int.zero).apply {
    hourOfDay = when {
        hourOfDay < 6 -> {
            date -= Int.one; 18
        }

        hourOfDay < 18 -> 6
        else -> 18
    }
}
