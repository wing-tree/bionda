package wing.tree.bionda.theme

import androidx.compose.ui.graphics.Color
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.six
import wing.tree.bionda.data.extension.thirty

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val LightSkyBlue = Color(0xFF87CEFA)

val YellowOrange = Color(0xFFFFAE42)

val LightGray = Color(0xFFE5E5E5)
val Yellow = Color(0xFFFAD98F)
val Orange = Color(0xFFFD8D3C)
val Red = Color(0xFFC30000)
val Purple = Color(0xFF54248E)

val temperature = List(Int.six) {
    Color.hsl(
        hue = it.times(Float.thirty),
        Float.one,
        Float.half,
        Float.one
    )
}
