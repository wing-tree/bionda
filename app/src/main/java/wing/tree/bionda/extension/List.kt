package wing.tree.bionda.extension

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import wing.tree.bionda.data.extension.complement
import wing.tree.bionda.data.extension.floatOrNull
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.model.Chart
import wing.tree.bionda.model.Forecast

fun List<Forecast.Item>.toTmpOffsets(
    chart: Chart,
    density: Float
): List<Offset> {
    if (isEmpty()) {
        return emptyList()
    }

    val maxTmp = maxOf {
        it.tmp?.floatOrNull ?: Float.zero
    }

    val segment = chart.segment
    val height = chart.tmp.chart.height
    val tmps = map {
        it.tmp?.toFloat() ?: Float.zero
    }

    fun Dp.toPx() = value.times(density)

    return buildList {
        add(tmps.first())

        tmps.forEachIndexed { index, tmp ->
            if (index < tmps.lastIndex) {
                add(tmp.plus(tmps[index.inc()]).half)
            }
        }

        add(tmps.last())
    }
        .mapIndexed { index, tmp ->
            val x = segment.width.times(index).toPx()
            val y = tmp
                .div(maxTmp)
                .complement
                .times(height.toPx())

            Offset(x, y)
        }
}
