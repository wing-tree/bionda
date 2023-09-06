package wing.tree.bionda.data.core

import android.icu.util.Calendar
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.data.top.level.may
import wing.tree.bionda.data.top.level.september

enum class Season {
    SUMMER,
    WINTER
}

val season = when (koreaCalendar) {
    in may {
        getActualMinimum(Calendar.DAY_OF_MONTH)
    }..september {
        getActualMaximum(Calendar.DAY_OF_MONTH)
    } -> Season.SUMMER

    else -> Season.WINTER
}
