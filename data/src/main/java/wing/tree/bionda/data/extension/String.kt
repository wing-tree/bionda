@file:Suppress("unused")

package wing.tree.bionda.data.extension

import android.icu.text.SimpleDateFormat
import wing.tree.bionda.data.top.level.koreaCalendar
import java.util.Locale

val String.Companion.degree: String get() = "°"
val String.Companion.empty: String get() = ""
val String.Companion.minusSign: String get() = "-"
val String.Companion.zero: String get() = "0"
val String.floatOrNull: Float? get() = toFloatOrNull()
val String.floatOrZero: Float get() = toFloatOrNull() ?: Float.zero
val String.int: Int get() = toInt()
val String.intOrZero: Int get() = toIntOrNull() ?: Int.zero

fun String.advanceHourOfDayBy(hourOfDay: Int, pattern: String): String {
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.KOREA)

    return koreaCalendar(simpleDateFormat.parse(this)).let {
        it.hourOfDay -= hourOfDay

        simpleDateFormat.format(it)
    }
}

fun String.ifZero(defaultValue: () -> String) = if (this `is` String.zero) {
    defaultValue()
} else {
    this
}

fun String.isBlankOrZero(): Boolean = isBlank() || `is`(String.zero)
