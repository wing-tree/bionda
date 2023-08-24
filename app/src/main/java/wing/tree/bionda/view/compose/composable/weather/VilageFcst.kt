package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.model.Address
import wing.tree.bionda.model.VilageFcst
import wing.tree.bionda.view.compose.composable.core.DegreeText
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.compose.composable.core.TextClock
import wing.tree.bionda.view.compose.composable.core.VerticalSpacer
import wing.tree.bionda.view.state.VilageFcstState

@Composable
fun VilageFcst(
    state: VilageFcstState,
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
            VilageFcstState.Loading -> Loading(modifier = Modifier)
            is VilageFcstState.Content -> Content(
                address = it.address,
                vilageFcst = it.vilageFcst,
                modifier = Modifier.fillMaxWidth()
            )

            is VilageFcstState.Error -> {
                Text(text = "${it.throwable}")
            }
        }
    }
}

@Composable
private fun Content(
    address: Address?,
    vilageFcst: VilageFcst,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Header(
            address = address,
            currentItem = vilageFcst.currentItem,
            modifier = Modifier.fillMaxWidth()
        )

        VerticalSpacer(16.dp)

        // TODO 아래 내용 체크, 기본 api 제공도 확인,
//        contentPadding = windowSizeClass.marginValues.copy(
//            top = Dp.zero,
//            bottom = Dp.zero
//        )

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp) // todo, style에서 계산 필요. or requireHeight 등도 확인.
        ) {
            Chart(
                items = vilageFcst.items,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun Header(
    address: Address?,
    currentItem: VilageFcst.Item?,
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
                    DegreeText(
                        text = it,
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
