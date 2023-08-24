package wing.tree.bionda.extension

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import wing.tree.bionda.data.extension.complement
import wing.tree.bionda.data.extension.floatOrNull
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.inc
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.model.style.ChartStyle
import wing.tree.bionda.model.VilageFcst

fun List<VilageFcst.Item>.toTmpOffsets(
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

    val tmps = map {
        it.tmp?.toFloat() ?: Float.zero
    }

    fun Dp.toPx() = value.times(density)

    return buildList {
        add(tmps.first())

        tmps.forEachIndexed { index, tmp ->
            if (index < tmps.lastIndex) {
                add(tmp.plus(tmps[index.inc]).half)
            }
        }

        add(tmps.last())
    }
        .mapIndexed { index, tmp ->
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
