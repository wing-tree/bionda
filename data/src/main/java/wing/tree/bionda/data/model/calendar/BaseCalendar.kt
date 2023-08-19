package wing.tree.bionda.data.model.calendar

import android.icu.util.Calendar
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.baseTime
import wing.tree.bionda.data.extension.cloneAsCalendar
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.regular.baseCalendar

@JvmInline
value class BaseCalendar(val value: Calendar = baseCalendar()) {
    val baseDate: String get() = value.baseDate
    val baseTime: String get() = value.baseTime

    fun previous() = value.cloneAsCalendar().apply {
        hourOfDay -= 3
    }.let {
        BaseCalendar(it)
    }
}
