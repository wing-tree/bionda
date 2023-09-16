package wing.tree.bionda.data.core

import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.dayAfterTomorrow
import wing.tree.bionda.data.extension.tomorrow
import wing.tree.bionda.data.top.level.koreaCalendar

sealed class Day {
    abstract val baseDate: String

    object DayAfterTomorrow : Day() {
        override val baseDate: String
            get() = koreaCalendar.dayAfterTomorrow.baseDate
    }

    object Today : Day() {
        override val baseDate: String
            get() = koreaCalendar.baseDate
    }

    object Tomorrow : Day() {
        override val baseDate: String
            get() = koreaCalendar.tomorrow.baseDate
    }
}
