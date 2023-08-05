package wing.tree.bionda.view.compose.composable

import android.icu.text.SimpleDateFormat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import wing.tree.bionda.data.extension.celsius
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.regular.fcstCalendar
import wing.tree.bionda.extension.zero
import wing.tree.bionda.model.Address
import wing.tree.bionda.model.Forecast
import wing.tree.bionda.model.WindowSizeClass
import wing.tree.bionda.view.state.ForecastState
import java.util.Locale

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
        Address(
            address = address,
            item = forecast.items.first(),
            modifier = Modifier.fillMaxWidth()
        )

        VerticalSpacer(
            height = when (windowSizeClass) {
                is WindowSizeClass.Compact -> 16.dp
                else -> 24.dp
            }
        )

        Items(
            items = forecast.items,
            contentPadding = windowSizeClass.marginValues.copy(
                top = Dp.zero,
                bottom = Dp.zero
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Address(
    address: Address?,
    item: Forecast.Item,
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
            item.tmp?.let {
                Text(
                    text = "$it${String.celsius}",
                    style = typography.displayLarge
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

            val thoroughfare = address?.thoroughfare

            if (thoroughfare.isNotNull()) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = thoroughfare)
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null
                    )
                }
            }
        }

        Text("TODO: REMOVE", modifier = Modifier.weight(Float.one))
    }
}

@Composable
private fun Items(
    items: ImmutableList<Forecast.Item>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            Item(item = item)
        }
    }
}

@Composable
private fun Item(
    item: Forecast.Item,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val fcstCalendar = fcstCalendar(item.fcstHour)
        val simpleDateFormat = SimpleDateFormat("a h시", Locale.KOREA)

        Text(
            text = simpleDateFormat.format(fcstCalendar),
            style = typography.labelSmall
        )

        if (item.pty.code `is` String.zero) {
            item.sky.value?.let {
                Text(text = it)
            }

            item.weatherIcon.sky[item.sky.code]?.let { drawableRes ->
                Icon(
                    painter = painterResource(id = drawableRes),
                    contentDescription = null
                )
            }
        } else {
            item.pty.value?.let {
                Text(text = it)
            }

            item.weatherIcon.pty[item.pty.code]?.let { drawableRes ->
                Icon(
                    painter = painterResource(id = drawableRes),
                    contentDescription = null
                )
            }
        }

        item.tmp?.let {
            Text(text = "$it${String.celsius}")
        }

        item.reh?.let {
            Text(text = it)
        }
    }
}
