package wing.tree.bionda.data.regular

import android.icu.util.Calendar
import wing.tree.bionda.data.extension.HALF_AN_HOUR
import wing.tree.bionda.data.extension.ONE
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.minute
import java.util.Locale

fun baseCalendar(): Calendar = koreaCalendar().apply {
    if (minute < Int.HALF_AN_HOUR) {
        hourOfDay -= Int.ONE
    }

    minute = Int.HALF_AN_HOUR
}

fun koreaCalendar(): Calendar = Calendar.getInstance(Locale.KOREA)

fun requestCalendar(): Calendar = koreaCalendar().apply {
    minute = when (minute) {
        in 0..4 -> {
            hourOfDay -= Int.ONE
            55
        }

        in 5..14 -> 5
        in 15..24 -> 15
        in 25..34 -> 25
        in 35..44 -> 35
        in 45..54 -> 45
        in 55..59 -> 55
        else -> minute
    }
}
