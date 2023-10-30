package wing.tree.bionda.view.compose.composable.weather

import android.graphics.PointF
import android.icu.text.SimpleDateFormat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import wing.tree.bionda.R
import wing.tree.bionda.data.constant.HYPHEN
import wing.tree.bionda.data.core.Day
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.floatOrNull
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.not
import wing.tree.bionda.data.extension.string
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.extension.drawDay
import wing.tree.bionda.extension.drawFcstTime
import wing.tree.bionda.extension.drawFeelsLikeTemperature
import wing.tree.bionda.extension.drawPcp
import wing.tree.bionda.extension.drawPop
import wing.tree.bionda.extension.drawReh
import wing.tree.bionda.extension.drawTmp
import wing.tree.bionda.extension.drawTmpChart
import wing.tree.bionda.extension.drawVec
import wing.tree.bionda.extension.drawWeatherIcon
import wing.tree.bionda.extension.drawWsd
import wing.tree.bionda.extension.offsets
import wing.tree.bionda.model.VilageFcst
import wing.tree.bionda.model.style.VilageFcstStyle
import wing.tree.bionda.view.compose.composable.core.Error
import wing.tree.bionda.view.compose.composable.core.Loading
import java.util.Locale
import kotlin.math.min

@Composable
fun VilageFcst(
    state: State<VilageFcst>,
    modifier: Modifier = Modifier
) {
    val style = VilageFcstStyle.defaultValue

    ElevatedCard(modifier = modifier.height(style.calculateHeight())) {
        AnimatedContent(
            targetState = state,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = String.empty,
            contentKey = {
                it::class.qualifiedName
            }
        ) {
            when (it) {
                State.Loading -> Loading(modifier = Modifier)
                is Complete -> when (it) {
                    is Complete.Success -> Content(
                        vilageFcst = it.value,
                        style = style,
                        modifier = Modifier.fillMaxWidth()
                    )

                    is Complete.Failure -> Error(it.exception)
                }
            }
        }
    }
}

@Composable
private fun Content(
    vilageFcst: VilageFcst,
    style: VilageFcstStyle,
    modifier: Modifier = Modifier
) {
    Chart(
        items = vilageFcst.items,
        style = style,
        modifier = modifier.padding(vertical = 12.dp)
    )
}

@Composable
private fun Chart(
    items: ImmutableList<VilageFcst.Item>,
    style: VilageFcstStyle,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val count = items.count()
    val marked = items.groupBy {
        it.fcstDate
    }.map { (_, value) ->
        value.minBy { item ->
            item.fcstTime
        }
    }

    val segment = style.segment

    val scrollState = rememberScrollState()
    val simpleDateFormat = remember {
        SimpleDateFormat(
            context.getString(R.string.pattern_fcst_hour),
            Locale.KOREA
        )
    }

    Row(modifier = modifier.horizontalScroll(scrollState)) {
        Canvas(modifier = Modifier.width(segment.width.times(count))) {
            val path = Path()
            val offsets = items.offsets(
                density = density,
                style = style
            )

            items.forEachIndexed { index, item ->
                val point = PointF(
                    segment.width
                        .times(index).toPx()
                        .plus(segment.width.toPx().half),
                    Float.zero
                )

                val day = if (item in marked) {
                    val resId = when (item.fcstDate) {
                        Day.Today.baseDate -> R.string.today
                        Day.Tomorrow.baseDate -> R.string.tomorrow
                        Day.DayAfterTomorrow.baseDate -> R.string.day_after_tomorrow
                        else -> null
                    }

                    resId?.let {
                        context.getString(it)
                    }
                } else {
                    null
                }

                drawDay(
                    day = day ?: String.empty,
                    point = point,
                    vilageFcstStyle = style.day
                )

                drawFcstTime(
                    fcstTime = simpleDateFormat.format(
                        koreaCalendar(hourOfDay = item.hourOfDay)
                    ),
                    point = point,
                    vilageFcstStyle = style.fcstTime
                )

                drawWeatherIcon(
                    weatherIcon = item.weatherIcon,
                    context = context,
                    point = point,
                    style = style.weatherIcon
                )

                drawTmp(
                    tmp = item.tmp ?: String.empty,
                    point = point,
                    offset = with(min(offsets[index].y, offsets[index.inc()].y)) {
                        offsets[index].copy(y = this)
                    },
                    vilageFcstStyle = style.tmp
                )

                drawTmpChart(
                    index = index,
                    offsets = offsets.map {
                        it.copy(y = it.y.plus(point.y))
                    },
                    path = path,
                    pointF = point,
                    style = style.tmpChart,
                )

                drawFeelsLikeTemperature(
                    feelsLikeTemperature = item.feelsLikeTemperature?.string ?: String.empty,
                    point = point,
                    vilageFcstStyle = style.feelsLikeTemperature
                )

                drawPcp(
                    pcp = item.pcp.takeIf {
                        it.not("강수없음")
                    }
                        ?: HYPHEN,
                    point = point,
                    vilageFcstStyle = style.pcp
                )

                drawPop(
                    pop = item.pop ?: String.empty,
                    point = point,
                    vilageFcstStyle = style.pop
                )

                drawReh(
                    reh = item.reh ?: String.empty,
                    point = point,
                    vilageFcstStyle = style.reh
                )

                drawVec(
                    vec = item.vec?.floatOrNull,
                    context = context,
                    point = point,
                    vilageFcstStyle = style.vec
                )

                drawWsd(
                    wsd = item.wsd ?: String.empty,
                    point = point,
                    vilageFcstStyle = style.wsd
                )
            }
        }
    }
}
