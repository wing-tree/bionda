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
import wing.tree.bionda.data.extension.toBin
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.model.UltraSrtFcst as UltraSrtFcstModel
import wing.tree.bionda.data.model.UltraSrtNcst as UltraSrtNcstModel
import wing.tree.bionda.data.model.VilageFcst as VilageFcstModel

sealed interface CalendarDecorator : (Calendar) -> Calendar {
    sealed interface Base : CalendarDecorator {
        object UltraSrtFcst : Base {
            override fun invoke(calendar: Calendar): Calendar = calendar.apply {
                val interval = UltraSrtFcstModel.interval

                if (minute < 45) { // TODO 45를 제공시간 변수로.
                    hourOfDay -= interval
                }

                minute = Int.halfAnHour
            }
        }

        object UltraSrtNcst : Base {
            override fun invoke(calendar: Calendar): Calendar = calendar.apply {
                val interval = UltraSrtNcstModel.interval

                if (minute < 40) { // TODO 40을 제공시간 프로퍼티로.
                    hourOfDay -= interval
                }

                minute = Int.zero
            }
        }

        object VilageFcst : Base {
            override fun invoke(calendar: Calendar): Calendar = calendar.apply {
                val interval = VilageFcstModel.interval

                if (minute < Int.ten) { // TODO ten을 제공시간으로.
                    if (hourOfDay.inc.rem(interval).isZero()) {
                        hourOfDay -= interval
                    }
                }

                if (hourOfDay < interval.dec) {
                    date -= Int.one
                }

                hourOfDay = hourOfDay.toBin(2..23, interval)

                minute = Int.zero
            }
        }
    }
}
