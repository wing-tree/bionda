package wing.tree.bionda.extension

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.complement
import wing.tree.bionda.data.extension.dec
import wing.tree.bionda.data.extension.floatOrNull
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.ifZero
import wing.tree.bionda.data.extension.inc
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.top.level.dayAfterTomorrow
import wing.tree.bionda.data.top.level.tomorrow
import wing.tree.bionda.model.style.ChartStyle
import wing.tree.bionda.model.VilageFcst

fun List<VilageFcst.Item>.dayAfterTomorrow() = with(dayAfterTomorrow) {
    val baseDate = baseDate

    filter {
        it.fcstDate `is` baseDate
    }
}

fun List<VilageFcst.Item>.offsets(
    density: Float,
    style: ChartStyle
): List<Offset> {
    if (isEmpty()) {
        return emptyList()
    }

    val segment = style.segment
    val height = style.tmpChart.height
    val maxTmp = maxOf {
        it.tmp?.floatOrNull ?: Float.zero
    }

    val minTmp = minOf {
        it.tmp?.floatOrNull ?: Float.zero
    }

    fun Dp.toPx() = value.times(density)

    return mapIndexed { index, item ->
        val tmp = item.tmp?.floatOrNull ?: Float.zero

        tmp.ifZero {
            val previous = getOrNull(index.dec)?.tmp?.floatOrNull ?: Float.zero
            val next = getOrNull(index.inc)?.tmp?.floatOrNull ?: Float.zero

            previous.plus(next).half
        }
    }.let {
        buildList {
            add(it.first())

            it.forEachIndexed { index, tmp ->
                if (index < it.lastIndex) {
                    add(tmp.plus(it[index.inc]).half)
                }
            }

            add(it.last())
        }.mapIndexed { index, tmp ->
            val x = segment
                .width
                .times(index)
                .toPx()

            val y = tmp
                .minus(minTmp)
                .div(maxTmp.minus(minTmp))
                .complement
                .times(height.toPx())

            Offset(x, y)
        }
    }
}

fun List<VilageFcst.Item>.tomorrow() = with(tomorrow) {
    val baseDate = baseDate

    filter {
        it.fcstDate `is` baseDate
    }
}
