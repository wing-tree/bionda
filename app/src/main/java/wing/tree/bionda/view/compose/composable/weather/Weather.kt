package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wing.tree.bionda.model.WindowSizeClass
import wing.tree.bionda.view.state.WeatherState

@Composable
fun Weather(
    state: WeatherState,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        VilageFcst(
            state = state.vilageFcstState,
            windowSizeClass = windowSizeClass,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
