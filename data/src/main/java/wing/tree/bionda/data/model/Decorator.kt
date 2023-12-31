package wing.tree.bionda.data.model

import wing.tree.bionda.data.extension.date
import wing.tree.bionda.data.extension.halfAnHour
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.inc
import wing.tree.bionda.data.extension.isZero
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.ten
import wing.tree.bionda.data.extension.toBin
import wing.tree.bionda.data.extension.zero

sealed interface Decorator<T> : (T) -> T {
    sealed interface Calendar : Decorator<android.icu.util.Calendar> {
        object UltraSrtFcst : Calendar {
            override fun invoke(calendar: android.icu.util.Calendar) = calendar.apply {
                if (minute < 45) { // TODO 45를 제공시간 변수로.
                    hourOfDay -= Int.one
                }

                minute = Int.halfAnHour
            }
        }

        object UltraSrtNcst : Calendar {
            override fun invoke(calendar: android.icu.util.Calendar) = calendar.apply {
                if (minute < 40) { // TODO 40을 제공시간 프로퍼티로.
                    hourOfDay -= Int.one
                }

                minute = Int.zero
            }
        }

        object UVIdx : Calendar {
            override fun invoke(calendar: android.icu.util.Calendar) = calendar.apply {
                hourOfDay = hourOfDay.toBin(0..24, 3)
                minute = Int.zero
            }
        }

        object VilageFcst : Calendar {
            override fun invoke(calendar: android.icu.util.Calendar) = calendar.apply {
                if (minute < Int.ten) { // TODO ten을 제공시간으로.
                    if (hourOfDay.inc.rem(3).isZero()) {
                        hourOfDay -= 3
                    }
                }

                if (hourOfDay < 2) {
                    date -= Int.one
                }

                hourOfDay = hourOfDay.toBin(2..23, 3)
                minute = Int.zero
            }
        }
    }
}
