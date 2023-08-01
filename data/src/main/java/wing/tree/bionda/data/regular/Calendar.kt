package wing.tree.bionda.data.regular

import android.icu.util.Calendar
import wing.tree.bionda.data.extension.HALF_AN_HOUR
import wing.tree.bionda.data.extension.ONE
import wing.tree.bionda.data.extension.TEN
import wing.tree.bionda.data.extension.ZERO
import wing.tree.bionda.data.extension.date
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.data.model.DetailedFunction
import java.util.Locale

fun apiDeliveryCalendar(
    detailFunction: DetailedFunction
): Calendar = koreaCalendar().apply {
    when (detailFunction) {
        DetailedFunction.UltraSrtFcst -> {
            minute = when (minute) {
                in 5 until 15 -> 5
                in 15 until 25 -> 15
                in 25 until 35 -> 25
                in 35 until 45 -> 35
                in 45 until 55 -> 45
                in 55 until 60 -> 55
                else -> {
                    hourOfDay -= Int.ONE
                    55
                }
            }
        }

        DetailedFunction.VilageFcst -> {
            hourOfDay = when (hourOfDay) {
                in 2 until 5 -> 2
                in 5 until 8 -> 5
                in 8 until 11 -> 8
                in 11 until 14 -> 11
                in 14 until 17 -> 14
                in 17 until 20 -> 17
                in 20 until 23 -> 20
                else -> {
                    date -= Int.ONE
                    23
                }
            }

            minute = Int.TEN
        }
    }
}

fun baseCalendar(
    detailFunction: DetailedFunction
): Calendar = koreaCalendar().apply {
    when (detailFunction) {
        DetailedFunction.UltraSrtFcst -> {
            if (minute < Int.HALF_AN_HOUR) {
                hourOfDay -= Int.ONE
            }

            minute = Int.HALF_AN_HOUR
        }

        DetailedFunction.VilageFcst -> {
            hourOfDay = when (hourOfDay) {
                in 2 until 5 -> 2
                in 5 until 8 -> 5
                in 8 until 11 -> 8
                in 11 until 14 -> 11
                in 14 until 17 -> 14
                in 17 until 20 -> 17
                in 20 until 23 -> 20
                else -> {
                    date -= Int.ONE
                    23
                }
            }

            minute = Int.ZERO
        }
    }
}

fun koreaCalendar(): Calendar = Calendar.getInstance(Locale.KOREA)
