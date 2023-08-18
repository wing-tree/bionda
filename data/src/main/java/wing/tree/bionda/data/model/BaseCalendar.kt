package wing.tree.bionda.data.model

import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.baseTime
import wing.tree.bionda.data.extension.cloneAsBaseCalendar
import wing.tree.bionda.data.extension.cloneAsCalendar
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.data.regular.koreaCalendar
import wing.tree.bionda.data.top.level.baseDateFormat
import wing.tree.bionda.data.top.level.baseTimeFormat

class BaseCalendar() {
    private val baseCalendar = koreaCalendar().cloneAsBaseCalendar()

    constructor(baseDate: String, baseTime: String): this() {
        baseCalendar.apply {
            koreaCalendar(baseDateFormat.parse(baseDate)).also {
                with(koreaCalendar(baseTimeFormat.parse(baseTime))) {
                    it.hourOfDay = hourOfDay
                    it.minute = minute
                }
            }
        }
    }

    val baseDate = baseCalendar.baseDate
    val baseTime = baseCalendar.baseTime

    fun previous() = baseCalendar.cloneAsCalendar().apply {
        hourOfDay -= 3
    }
}
