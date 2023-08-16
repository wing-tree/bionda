package wing.tree.bionda.view.compose.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.extension.degree
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.model.Address
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.model.WindowSizeClass
import wing.tree.bionda.view.state.ForecastState

@Composable
fun Forecast(
    state: ForecastState,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state,
        modifier = modifier,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = String.empty,
        contentKey = {
            it::class.qualifiedName
        }
    ) {
        when (it) {
            ForecastState.Loading -> Loading(modifier = Modifier)

            is ForecastState.Content -> Content(
                address = it.address,
                forecast = it.forecast,
                windowSizeClass = windowSizeClass,
                modifier = Modifier.fillMaxWidth()
            )

            is ForecastState.Error -> {
                Column {
                    Text(text = "${it.throwable}")
                }
            }
        }
    }
}

@Composable
private fun Content(
    address: Address?,
    forecast: Forecast,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Header(
            address = address,
            currentItem = forecast.currentItem,
            modifier = Modifier.fillMaxWidth()
        )

        VerticalSpacer(
            height = when (windowSizeClass) {
                is WindowSizeClass.Compact -> 16.dp
                else -> 24.dp
            }
        )

        // TODO 아래 내용 체크, 기본 api 제공도 확인,
//        contentPadding = windowSizeClass.marginValues.copy(
//            top = Dp.zero,
//            bottom = Dp.zero
//        )
        Chart(
            items = forecast.items,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )
    }
}

@Composable
private fun Header(
    address: Address?,
    currentItem: Forecast.Item?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(Float.one),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextClock()
            VerticalSpacer(height = 8.dp)
            Address(address = address)
        }

        Column(
            modifier = Modifier.weight(Float.one),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            currentItem?.let { item ->
                item.tmp?.let {
                    Text(
                        text = "$it${String.degree}",
                        style = typography.headlineLarge
                    )
                }

                if (item.pty.code `is` String.zero) {
                    item.sky.value?.let {
                        Text(text = it)
                    }
                } else {
                    item.pty.value?.let {
                        Text(text = it)
                    }
                }
            }
        }
    }
}

@Composable
private fun Address(
    address: Address?,
    modifier: Modifier = Modifier
) {
    val thoroughfare = address?.thoroughfare

    if (thoroughfare.isNotNull()) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = thoroughfare)
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
