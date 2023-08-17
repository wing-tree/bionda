package wing.tree.bionda.data.model

import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.baseTime
import wing.tree.bionda.data.extension.cloneAsBaseCalendar
import wing.tree.bionda.data.extension.cloneAsCalendar
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.regular.koreaCalendar

class BaseCalendar {
    private val baseCalendar = koreaCalendar().cloneAsBaseCalendar()

    val baseDate = baseCalendar.baseDate
    val baseTime = baseCalendar.baseTime

    fun previous() = baseCalendar.cloneAsCalendar().apply {
        hourOfDay -= 3
    }
}
