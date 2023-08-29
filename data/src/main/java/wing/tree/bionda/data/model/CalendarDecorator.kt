package wing.tree.bionda.data.model

import android.icu.util.Calendar
import wing.tree.bionda.data.extension.date
import wing.tree.bionda.data.extension.dec
import wing.tree.bionda.data.extension.halfAnHour
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.inc
import wing.tree.bionda.data.extension.isZero
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.ten
import wing.tree.bionda.data.extension.zero

sealed interface CalendarDecorator : (Calendar) -> Calendar {
    sealed interface Base : CalendarDecorator {
        object UltraSrtFcst : Base {
            override fun invoke(calendar: Calendar): Calendar = calendar.apply {
                if (minute < 45) {
                    hourOfDay -= Int.one
                }

                minute = Int.halfAnHour
            }
        }

        object UltraSrtNcst : Base {
            override fun invoke(calendar: Calendar): Calendar = calendar.apply {
                if (minute < 40) {
                    hourOfDay -= Int.one
                }

                minute = Int.zero
            }
        }

        object VilageFcst : Base {
            override fun invoke(calendar: Calendar): Calendar = calendar.apply {
                if (minute < Int.ten) {
                    if (hourOfDay.inc.rem(3).isZero()) {
                        hourOfDay -= 3
                    }
                }

                hourOfDay = if (hourOfDay < 2) {
                    date -= Int.one; 23
                } else {
                    with(hourOfDay.inc.div(3)) {
                        times(3).dec
                    }
                }

                minute = Int.zero
            }
        }
    }
}
