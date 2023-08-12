package wing.tree.bionda.top.level

import android.icu.text.DateFormatSymbols
import android.icu.util.Calendar
import java.util.Locale

private val amPmStrings = DateFormatSymbols(Locale.KOREA).amPmStrings

val amString: String = amPmStrings[Calendar.AM]
val pmString: String = amPmStrings[Calendar.PM]
