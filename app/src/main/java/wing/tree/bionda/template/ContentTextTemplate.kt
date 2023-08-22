package wing.tree.bionda.template

import android.content.Context
import android.icu.util.Calendar
import androidx.annotation.StringRes
import wing.tree.bionda.R
import wing.tree.bionda.data.HangulJamo.consonants
import wing.tree.bionda.data.HangulJamo.jamo
import wing.tree.bionda.data.constant.COMMA
import wing.tree.bionda.data.constant.NEWLINE
import wing.tree.bionda.data.constant.SPACE
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.hour
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.ifZero
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.not
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.second
import wing.tree.bionda.data.regular.koreaCalendar
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.top.level.amString
import wing.tree.bionda.top.level.pmString
import java.time.LocalTime

sealed class ContentTextTemplate {
    class PtyOrSky(private val context: Context) : ContentTextTemplate() {
        private fun getString(@StringRes resId: Int) = context.getString(resId)
        private fun getSubjectMarker(subject: String): String {
            val jamo = subject.jamo
            val subjectMarkers = context.resources.getStringArray(R.array.subject_markers)

            return when {
                consonants.any {
                    jamo.endsWith(it)
                } -> subjectMarkers.first()

                else -> subjectMarkers.second()
            }
        }

        operator fun invoke(forecast: Forecast): String {
            val koreaCalendar = koreaCalendar()
            val separator = "$COMMA$SPACE"

            return forecast.items
                .filter {
                    koreaCalendar.predicate(it)
                }.groupBy {
                    it.pty.value ?: it.sky.value
                }.toList()
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
                }.let {
                    val subject = "${it.dropLast(Int.one).last()}"
                    val subjectMarker = getSubjectMarker(subject)
                    val replacement = "$subjectMarker 올 예정입니다."

                    it.replace(Regex("$COMMA$"), replacement)
                }
        }
    }

    protected fun Calendar.predicate(item: Forecast.Item): Boolean {
        if (item.fcstDate not baseDate.int) {
            return false
        }

        if (item.fcstHour < hourOfDay) {
            return false
        }

        return true
    }
}
