package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
    val paddingValues = windowSizeClass.marginValues

    Column(modifier = modifier.padding(paddingValues)) {
        VilageFcst(
            state = state.vilageFcstState,
            modifier = Modifier.fillMaxWidth()
        )

        MidLandFcstTa(
            state = state.midLandFcstTaState,
            modifier = Modifier.fillMaxSize()
        )
    }
}
