package wing.tree.bionda.data.top.level

import android.icu.text.SimpleDateFormat
import wing.tree.bionda.data.constant.PATTERN_BASE_DATE
import wing.tree.bionda.data.constant.PATTERN_BASE_TIME
import wing.tree.bionda.data.constant.PATTERN_DAY_OF_MONTH
import wing.tree.bionda.data.constant.PATTERN_LOCDATE
import wing.tree.bionda.data.constant.PATTERN_TM_FC
import java.util.Locale

val baseDateFormat = SimpleDateFormat(PATTERN_BASE_DATE, Locale.KOREA)
val baseTimeFormat = SimpleDateFormat(PATTERN_BASE_TIME, Locale.KOREA)
val dayOfMonthFormat = SimpleDateFormat(PATTERN_DAY_OF_MONTH, Locale.KOREA)
val locdateFormat = SimpleDateFormat(PATTERN_LOCDATE, Locale.KOREA)
val tmFcFormat = SimpleDateFormat(PATTERN_TM_FC, Locale.KOREA)
