package wing.tree.bionda.theme

import android.app.Activity
import android.view.View
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = LightSkyBlue,
    tertiary = Pink40,
    background = Background,
    onBackground = Color.Black,
    surface = Surface
)

@Composable
fun BiondaTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    if (view.isNotInEditMode) {
        SideEffect {
            val context = view.context

            if (context is Activity) {
                val insetsController = WindowCompat.getInsetsController(
                    context.window,
                    view
                )

                insetsController.isAppearanceLightStatusBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

private val View.isNotInEditMode: Boolean get() = isInEditMode.not()
