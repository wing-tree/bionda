package wing.tree.bionda.data.core

import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.top.level.dayAfterTomorrow
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.data.top.level.tomorrow

sealed class Day {
    abstract val baseDate: String

    object DayAfterTomorrow : Day() {
        override val baseDate: String
            get() = dayAfterTomorrow.baseDate
    }

    object Today : Day() {
        override val baseDate: String
            get() = koreaCalendar.baseDate
    }

    object Tomorrow : Day() {
        override val baseDate: String
            get() = tomorrow.baseDate
    }
}
