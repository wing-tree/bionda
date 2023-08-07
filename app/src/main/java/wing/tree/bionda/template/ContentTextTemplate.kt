package wing.tree.bionda.template

import android.content.Context
import android.icu.text.DateFormatSymbols
import android.icu.util.Calendar
import androidx.annotation.StringRes
import wing.tree.bionda.R
import wing.tree.bionda.data.constant.COMMA
import wing.tree.bionda.data.constant.NEWLINE
import wing.tree.bionda.data.extension.hour
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.ifZero
import wing.tree.bionda.data.regular.koreaCalendar
import wing.tree.bionda.model.Forecast
import java.time.LocalTime
import java.util.Locale

class ContentTextTemplate(
    private val context: Context,
    private val forecast: Forecast
) {
    enum class Category {
        PTY_OR_SKY
    }

    private val amPmStrings = DateFormatSymbols(Locale.KOREA).amPmStrings
    private val amString = amPmStrings[Calendar.AM]
    private val pmString = amPmStrings[Calendar.PM]


    operator fun invoke(category: String) {

    }

    private fun getString(@StringRes resId: Int) = context.getString(resId)

    fun makePtyOrSky(): String {
        val koreaCalendar = koreaCalendar()

        return forecast.items.groupBy {
            it.pty.value ?: it.sky.value
        }
            .toList()
            .joinToString(separator = NEWLINE) { (value, items) ->
                val amHours = mutableListOf<Int>()
                val pmHours = mutableListOf<Int>()

                items.forEach { item ->
                    val hour = koreaCalendar.apply {
                        hourOfDay = item.fcstHour
                    }
                        .hour
                        .ifZero {
                            LocalTime.NOON.hour
                        }

                    if (LocalTime.NOON.hour > item.fcstHour) {
                        amHours.add(hour)
                    } else {
                        pmHours.add(hour)
                    }
                }

                buildString {
                    if (amHours.isNotEmpty()) {
                        append("$amString ")
                        append(amHours.joinToString(separator = "$COMMA ", postfix = getString(R.string.hour)))

                        if (pmHours.isNotEmpty()) {
                            append("$COMMA ")
                        }
                    }

                    if (pmHours.isNotEmpty()) {
                        append("$pmString ")
                        append(pmHours.joinToString(separator = "$COMMA ", postfix = getString(R.string.hour)))
                    }

                    append("${getString(R.string.at)} $value$COMMA")
                }
            }
                .replace(Regex("$COMMA$"), "가/이 올 예정입니다.")
    }
}
