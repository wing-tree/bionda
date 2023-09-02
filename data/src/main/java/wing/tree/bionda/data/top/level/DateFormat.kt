package wing.tree.bionda.data.top.level

import android.icu.text.SimpleDateFormat
import wing.tree.bionda.data.constant.PATTERN_BASE_DATE
import wing.tree.bionda.data.constant.PATTERN_BASE_TIME
import wing.tree.bionda.data.constant.PATTERN_DAY_OF_MONTH
import wing.tree.bionda.data.constant.PATTERN_LOCDATE
import wing.tree.bionda.data.constant.PATTERN_TIME_RANGE_FIRST
import wing.tree.bionda.data.constant.PATTERN_TIME_RANGE_LAST
import wing.tree.bionda.data.constant.PATTERN_TM_FC
import wing.tree.bionda.data.constant.PATTERN_UV_IDX_TIME
import java.util.Locale

val baseDateFormat = SimpleDateFormat(PATTERN_BASE_DATE, Locale.KOREA)
val baseTimeFormat = SimpleDateFormat(PATTERN_BASE_TIME, Locale.KOREA)
val dayOfMonthFormat = SimpleDateFormat(PATTERN_DAY_OF_MONTH, Locale.KOREA)
val locdateFormat = SimpleDateFormat(PATTERN_LOCDATE, Locale.KOREA)
val tmFcFormat = SimpleDateFormat(PATTERN_TM_FC, Locale.KOREA)
val uvIdxTimeFormat = SimpleDateFormat(PATTERN_UV_IDX_TIME, Locale.KOREA)
val timeRangeFirstFormat = SimpleDateFormat(PATTERN_TIME_RANGE_FIRST, Locale.KOREA)
val timeRangeLastFormat = SimpleDateFormat(PATTERN_TIME_RANGE_LAST, Locale.KOREA)
