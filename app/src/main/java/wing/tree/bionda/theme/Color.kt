package wing.tree.bionda.theme

import androidx.compose.ui.graphics.Color
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.six
import wing.tree.bionda.data.extension.thirty

object Dark {
    val Primary = Color(0xFFC1C1FF)
    val OnPrimary = Color(0xFF252478)
    val PrimaryContainer = Color(0xFF3C3D8F)
    val OnPrimaryContainer = Color(0xFFE1DFFF)
    val Secondary = Color(0xFFC6C4DD)
    val OnSecondary = Color(0xFF2F2F42)
    val SecondaryContainer = Color(0xFF454559)
    val OnSecondaryContainer = Color(0xFFE2E0F9)
    val Tertiary = Color(0xFFE9B9D3)
    val OnTertiary = Color(0xFF46263A)
    val TertiaryContainer = Color(0xFF5F3C51)
    val OnTertiaryContainer = Color(0xFFFFD8EC)
    val Error = Color(0xFFFFB4AB)
    val ErrorContainer = Color(0xFF93000A)
    val OnError = Color(0xFF690005)
    val OnErrorContainer = Color(0xFFFFDAD6)
    val Background = Color(0xFF1C1B1F)
    val OnBackground = Color(0xFFE5E1E6)
    val Outline = Color(0xFF918F9A)
    val InverseOnSurface = Color(0xFF1C1B1F)
    val InverseSurface = Color(0xFFE5E1E6)
    val InversePrimary = Color(0xFF5455A9)
    val SurfaceTint = Color(0xFFC1C1FF)
    val OutlineVariant = Color(0xFF47464F)
    val Scrim = Color(0xFF000000)
    val Surface = Color(0xFF131316)
    val OnSurface = Color(0xFFC8C5CA)
    val SurfaceVariant = Color(0xFF47464F)
    val OnSurfaceVariant = Color(0xFFC8C5D0)
}

object Light {
    val Primary = Color(0xFF5455A9)
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFE1DFFF)
    val OnPrimaryContainer = Color(0xFF0C0664)
    val Secondary = Color(0xFF5D5C72)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFE2E0F9)
    val OnSecondaryContainer = Color(0xFF1A1A2C)
    val Tertiary = Color(0xFF795369)
    val OnTertiary = Color(0xFFFFFFFF)
    val TertiaryContainer = Color(0xFFFFD8EC)
    val OnTertiaryContainer = Color(0xFF2F1124)
    val Error = Color(0xFFBA1A1A)
    val ErrorContainer = Color(0xFFFFDAD6)
    val OnError = Color(0xFFFFFFFF)
    val OnErrorContainer = Color(0xFF410002)
    val Background = Color(0xFFFFFBFF)
    val OnBackground = Color(0xFF1C1B1F)
    val Outline = Color(0xFF777680)
    val InverseOnSurface = Color(0xFFF3EFF4)
    val InverseSurface = Color(0xFF313034)
    val InversePrimary = Color(0xFFC1C1FF)
    val SurfaceTint = Color(0xFF5455A9)
    val OutlineVariant = Color(0xFFC8C5D0)
    val Scrim = Color(0xFF000000)
    val Surface = Color(0xFFFCF8FD)
    val OnSurface = Color(0xFF1C1B1F)
    val SurfaceVariant = Color(0xFFE4E1EC)
    val OnSurfaceVariant = Color(0xFF47464F)
}

val LightGray = Color(0xFFE5E5E5)
val Yellow = Color(0xFFFAD98F)
val Orange = Color(0xFFFD8D3C)
val Red = Color(0xFFC30000)
val Purple = Color(0xFF54248E)

val temperature = List(Int.six) { // todo midta의 스타일 클래스 프로퍼티 편입 가능.
    Color.hsl(
        hue = it.times(Float.thirty),
        Float.one,
        Float.half,
        Float.one
    )
}
