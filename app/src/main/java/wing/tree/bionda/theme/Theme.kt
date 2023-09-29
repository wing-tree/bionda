package wing.tree.bionda.theme

import android.app.Activity
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = LightSkyBlue,
    tertiary = Pink40
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple40,
    secondary = LightSkyBlue,
    tertiary = Pink40
)

@Composable
fun BiondaTheme(
    content: @Composable () -> Unit
) {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val colorScheme = when {
        isSystemInDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current

    if (view.isNotInEditMode) {
        SideEffect {
            val context = view.context

            if (context is Activity) {
                val insetsController = WindowCompat.getInsetsController(
                    context.window,
                    view
                )

                insetsController.isAppearanceLightStatusBars = isSystemInDarkTheme.not()
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private val View.isNotInEditMode: Boolean get() = isInEditMode.not()
