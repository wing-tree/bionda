package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
        modifier = modifier
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        VilageFcst(
            state = state.vilageFcst,
            modifier = Modifier.fillMaxWidth()
        )

        MidLandFcstTa(
            state = state.midLandFcstTa,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )

        LCRiseSetInfo(
            state = state.lcRiseSetInfo,
            modifier = Modifier.fillMaxWidth()
        )

        UVIdx(
            state = state.uvIdx,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
