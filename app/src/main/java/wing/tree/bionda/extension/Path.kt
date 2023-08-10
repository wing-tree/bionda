package wing.tree.bionda.extension

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.`is`


fun Path.quadraticBezierTo(
    index: Int,
    offsets: List<Offset>
) {
    val item = offsets[index]
    val x1 = offsets[index.dec()].x
    val y1 = offsets[index.dec()].y
    val x2: Float
    val y2: Float

    if (index `is` offsets.lastIndex) {
        x2 = item.x
        y2 = item.y
    } else {
        x2 = x1.plus(item.x).half
        y2 = y1.plus(item.y).half
    }

    quadraticBezierTo(
        x1 = x1,
        y1 = y1,
        x2 = x2,
        y2 = y2
    )
}
