package wing.tree.bionda.data.top.level

import android.icu.util.Calendar
import wing.tree.bionda.data.extension.cloneAsBaseCalendar
import wing.tree.bionda.data.extension.date
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.model.CalendarDecorator
import java.util.Date
import java.util.Locale

val koreaCalendar: Calendar get() = koreaCalendar()

fun baseCalendar(base: CalendarDecorator.Base): Calendar = koreaCalendar.cloneAsBaseCalendar(base)
fun fcstCalendar(hourOfDay: Int): Calendar = koreaCalendar.apply {
    this.hourOfDay = hourOfDay
}

fun koreaCalendar(block: Calendar.() -> Unit) = koreaCalendar.apply(block)
fun koreaCalendar(date: Date): Calendar = Calendar.getInstance(Locale.KOREA).apply {
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

fun koreaCalendarOf(
    timeInMillis: Long? = null
): Calendar = Calendar.getInstance().apply {
    timeInMillis?.let {
        this.timeInMillis = it
    }
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

fun uvIdxCalendar(time: String): Calendar = koreaCalendar(
    uvIdxTimeFormat.parse(time)
)
