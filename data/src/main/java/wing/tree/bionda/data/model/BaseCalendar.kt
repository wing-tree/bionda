package wing.tree.bionda.data.model

import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.baseTime
import wing.tree.bionda.data.extension.cloneAsBaseCalendar
import wing.tree.bionda.data.extension.cloneAsCalendar
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.regular.koreaCalendar

class BaseCalendar {
    private val value = koreaCalendar().cloneAsBaseCalendar()

    val baseDate: String get() = value.baseDate
    val baseTime: String get() = value.baseTime

    fun previous() = value.cloneAsCalendar().apply {
        hourOfDay -= 3
    }
}
