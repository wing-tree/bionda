package wing.tree.bionda.template

import android.content.Context
import androidx.annotation.StringRes
import wing.tree.bionda.R
import wing.tree.bionda.data.constant.COMMA
import wing.tree.bionda.data.constant.NEWLINE
import wing.tree.bionda.data.constant.SPACE
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.hour
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.ifZero
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.regular.koreaCalendar
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.top.level.amString
import wing.tree.bionda.top.level.pmString
import java.time.LocalTime

sealed class ContentTextTemplate {
    class PtyOrSky(private val context: Context) : ContentTextTemplate() {
        private fun getString(@StringRes resId: Int) = context.getString(resId)

        operator fun invoke(forecast: Forecast): String {
            val koreaCalendar = koreaCalendar()
            val separator = "$COMMA$SPACE"

            return forecast.items
                .filter {
                    it.fcstDate `is` koreaCalendar.baseDate.int
                }.groupBy {
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
                            append("$amString$SPACE")
                            append(amHours.joinToString(separator = separator, postfix = getString(R.string.hour)))

                            if (pmHours.isNotEmpty()) {
                                append(separator)
                            }
                        }

                        if (pmHours.isNotEmpty()) {
                            append("$pmString$SPACE")
                            append(pmHours.joinToString(separator = separator, postfix = getString(R.string.hour)))
                        }

                        append("${getString(R.string.at)} $value$COMMA")
                    }
                }
                    .replace(Regex("$COMMA$"), "가/이 올 예정입니다.")
        }
    }
}
