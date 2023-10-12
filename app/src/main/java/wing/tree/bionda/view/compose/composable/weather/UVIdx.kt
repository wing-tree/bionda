package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.extension.delayHourOfDayBy
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.ifTrue
import wing.tree.bionda.data.extension.intOrZero
import wing.tree.bionda.data.extension.isBlankOrZero
import wing.tree.bionda.data.extension.timeRange
import wing.tree.bionda.data.model.LivingWthrIdx
import wing.tree.bionda.data.model.LivingWthrIdx.UVIdx
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.data.top.level.timeFormat
import wing.tree.bionda.theme.LightGray
import wing.tree.bionda.theme.Orange
import wing.tree.bionda.theme.Purple
import wing.tree.bionda.theme.Red
import wing.tree.bionda.theme.Yellow
import wing.tree.bionda.view.compose.composable.core.Loading

@Composable
fun UVIdx(
    state: State<UVIdx>,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        AnimatedContent(
            targetState = state,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = String.empty,
            contentKey = {
                it::class.qualifiedName
            }
        ) {
            when (it) {
                State.Loading -> Loading(modifier = Modifier)
                is Complete -> when (it) {
                    is Complete.Success -> Content(uvIdx = it.value)
                    is Complete.Failure -> Text(it.exception.message ?: "${it.exception}")
                }
            }
        }
    }
}

@Composable
private fun Content(
    uvIdx: UVIdx,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        val date = uvIdx.item.date

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(uvIdx.items) { item ->
                Item(
                    item = item,
                    date = date
                )
            }
        }
    }
}

@Composable
private fun Item(
    item: LivingWthrIdx.H,
    date: String,
    modifier: Modifier = Modifier
) {
    val (n, h) = item
    val koreaCalendar = koreaCalendar(timeFormat.parse(date)).delayHourOfDayBy(n)
    val hourOfDay = koreaCalendar.hourOfDay

    when {
        hourOfDay in 6 until 18 -> true
        h.isBlankOrZero() -> false
        else -> true
    }.ifTrue {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = koreaCalendar.timeRange)
            H(h.intOrZero.coerceIn(0..11))
        }
    }
}

@Composable
private fun H(
    h: Int,
    modifier: Modifier = Modifier
) {
    // TODO chart처럼 style 정의 필요
    val height = 55.dp // todo remove
    val width = 5.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$h")
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .background(
                    brush = Brush.verticalGradient(
                        persistentListOf(
                            Purple,
                            Red,
                            Orange,
                            Yellow,
                            LightGray
                        )
                    ),
                    shape = CircleShape
                )
                .drawWithContent {
                    drawContent()

                    val color = when {
                        h >= 11 -> Purple
                        h >= 8 -> Red
                        h >= 6 -> Orange
                        h >= 3 -> Yellow
                        else -> LightGray
                    }

                    val y = height
                        .div(11)
                        .times(11.minus(h))

                    drawCircle(
                        color = color,
                        radius = 5.dp.toPx(),
                        center = Offset(width.toPx().half, y.toPx())
                    )
                }
        )
    }
}
