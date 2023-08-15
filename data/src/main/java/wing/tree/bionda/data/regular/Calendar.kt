package wing.tree.bionda.data.regular

import android.icu.util.Calendar
import wing.tree.bionda.data.extension.cloneAsBaseCalendar
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.minute
import java.util.Locale

fun baseCalendar(): Calendar = koreaCalendar().cloneAsBaseCalendar()

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
