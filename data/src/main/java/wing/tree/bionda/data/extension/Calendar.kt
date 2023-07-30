package wing.tree.bionda.data.extension

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import java.util.Locale

val Calendar.baseDate: String get() = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
    .format(time)

val Calendar.baseTime: String get() = SimpleDateFormat("HHmm", Locale.KOREA)
    .format(time)

var Calendar.hourOfDay: Int
    get() = get(Calendar.HOUR_OF_DAY)
    set(value) {
        set(Calendar.HOUR_OF_DAY, value)
    }

var Calendar.minute: Int
    get() = get(Calendar.MINUTE)
    set(value) {
        set(Calendar.MINUTE, value)
    }

val Calendar.requestDate: String get() = baseDate
val Calendar.requestTime: String get() = baseTime
