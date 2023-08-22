package wing.tree.bionda.data.model.calendar

import android.icu.util.Calendar
import wing.tree.bionda.data.extension.cloneAsCalendar
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.julianDay
import wing.tree.bionda.data.regular.tmFcCalendar
import wing.tree.bionda.data.top.level.tmFcFormat

@JvmInline
value class TmFcCalendar(val value: Calendar = tmFcCalendar()) {
    val julianDay: Int get() = value.julianDay
    val tmFc: String get() = tmFcFormat.format(value)

    fun previous() = value.cloneAsCalendar().apply {
        hourOfDay -= 12
    }.let {
        TmFcCalendar(it)
    }
}
