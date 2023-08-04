package wing.tree.bionda.view.compose.composable

import android.icu.text.SimpleDateFormat
import android.location.Address
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import wing.tree.bionda.data.extension.celsius
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.model.Category
import wing.tree.bionda.data.model.CodeValue
import wing.tree.bionda.data.regular.fcstCalendar
import wing.tree.bionda.extension.zero
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

            is ForecastState.Content -> {
                val contentPadding = windowSizeClass.marginValues.copy(
                    top = Dp.zero,
                    bottom = Dp.zero
                )

                Content(
                    address = it.address,
                    forecast = it.forecast,
                    contentPadding = contentPadding,
                    modifier = Modifier.fillMaxWidth()
                )
            }

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
    contentPadding: PaddingValues,
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

        Items(
            items = forecast.items,
            contentPadding = contentPadding,
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
    val pty = item.items[Category.VilageFcst.PTY]
    val sky = item.items[Category.VilageFcst.SKY]
    val tmp = item.items[Category.VilageFcst.TMP]
    val reh = item.items[Category.VilageFcst.REH]

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(Float.one),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            tmp?.let {
                Text(
                    text = "$it${String.celsius}",
                    style = typography.displayLarge
                )
            }

            if (pty `is` String.zero) {
                CodeValue.sky[sky]?.let {
                    Text(text = it)
                }
            } else {
                CodeValue.pty[pty]?.let {
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

        Text(text = simpleDateFormat.format(fcstCalendar))

        val pty = item.items[Category.VilageFcst.PTY]
        val sky = item.items[Category.VilageFcst.SKY]
        val tmp = item.items[Category.VilageFcst.TMP]
        val reh = item.items[Category.VilageFcst.REH]

        if (pty `is` String.zero) {
            CodeValue.sky[sky]?.let {
                Text(text = it)
            }
        } else {
            CodeValue.pty[pty]?.let {
                Text(text = it)
            }
        }

        tmp?.let {
            Text(text = "$it${String.celsius}")
        }

        reh?.let {
            Text(text = it)
        }
    }
}
