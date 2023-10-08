package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wing.tree.bionda.data.extension.complement
import wing.tree.bionda.data.extension.divAsFloat
import wing.tree.bionda.data.extension.halfAfFloat
import wing.tree.bionda.data.extension.oneSecondInMilliseconds
import wing.tree.bionda.extension.verticalFadingEdge
import wing.tree.bionda.model.WindowSizeClass
import wing.tree.bionda.view.compose.composable.UltraSrtNcst
import wing.tree.bionda.view.state.WeatherState
import wing.tree.bionda.view.state.WeatherState.Action

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Weather(
    state: WeatherState,
    windowSizeClass: WindowSizeClass,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val paddingValues = windowSizeClass.marginValues

    Column(modifier = modifier.padding(paddingValues)) {
        var refreshing by remember {
            mutableStateOf(false)
        }

        val pullRefreshState = rememberPullRefreshState(
            refreshing = refreshing,
            onRefresh = {
                coroutineScope.launch {
                    refreshing = true

                    onAction(Action.Refresh)
                    delay(Long.oneSecondInMilliseconds)

                    refreshing = false
                }
            },
        )

        val scrollState = rememberScrollState()

        Box(Modifier.pullRefresh(state = pullRefreshState)) {
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                modifier = Modifier.align(TopCenter)
            )

            Column(
                modifier = modifier
                    .verticalScroll(scrollState)
                    .verticalFadingEdge(scrollState = scrollState)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val livingWthrIdx = state.livingWthrIdx

                Column(
                    modifier = Modifier
                        .graphicsLayer {
                            with(scrollState) {
                                alpha = value.divAsFloat(maxValue).complement
                                translationY = value.halfAfFloat
                            }
                        }
                ) {
                    Address(
                        address = state.address,
                        modifier = Modifier.clickable {
                            onAction(Action.Click.Area)
                        }
                    )
                    
                    UltraSrtNcst(state = state.ultraSrtNcst)
                }

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
                    state = livingWthrIdx.uvIdx,
                    modifier = Modifier.fillMaxWidth()
                )

                AirDiffusionIdx(
                    state = livingWthrIdx.airDiffusionIdx,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
