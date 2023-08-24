package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wing.tree.bionda.model.WindowSizeClass
import wing.tree.bionda.view.state.WeatherState

@Composable
fun Weather(
    state: WeatherState,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    val paddingValues = windowSizeClass.marginValues

    Column(
        modifier = modifier.padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        VilageFcst(
            state = state.vilageFcstState,
            modifier = Modifier.fillMaxWidth()
        )

        MidLandFcstTa(
            state = state.midLandFcstTaState,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
    }
}
