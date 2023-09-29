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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.extension.complement
import wing.tree.bionda.data.extension.divAsFloat
import wing.tree.bionda.data.extension.halfAfFloat
import wing.tree.bionda.extension.verticalFadingEdge
import wing.tree.bionda.model.WindowSizeClass
import wing.tree.bionda.view.compose.composable.UltraSrtNcst
import wing.tree.bionda.view.state.WeatherState

@Composable
fun Weather(
    state: WeatherState,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    val paddingValues = windowSizeClass.marginValues

    Column(modifier = modifier.padding(paddingValues)) {
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .verticalScroll(scrollState)
                .verticalFadingEdge(scrollState = scrollState)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UltraSrtNcst(
                state = state.ultraSrtNcst,
                modifier = Modifier
                    .graphicsLayer {
                        with(scrollState) {
                            alpha = value.divAsFloat(maxValue).complement
                            translationY = value.halfAfFloat
                        }
                    }
            )

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

            UVIdx(
                state = state.uvIdx,
                modifier = Modifier.fillMaxWidth()
            )

            AirDiffusionIdx(
                state = state.airDiffusionIdx,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
