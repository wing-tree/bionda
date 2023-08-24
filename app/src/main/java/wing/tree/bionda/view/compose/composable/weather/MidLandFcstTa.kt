package wing.tree.bionda.view.compose.composable.weather

import android.icu.text.DateFormatSymbols
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.extension.date
import wing.tree.bionda.data.extension.dayOfWeek
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.float
import wing.tree.bionda.data.extension.isNull
import wing.tree.bionda.data.extension.julianDay
import wing.tree.bionda.data.model.weather.MidLandFcstTa.BothFailure
import wing.tree.bionda.data.model.weather.MidLandFcstTa.BothSuccess
import wing.tree.bionda.data.model.weather.MidLandFcstTa.OneOfSuccess
import wing.tree.bionda.data.regular.koreaCalendar
import wing.tree.bionda.data.top.level.dayOfMonthFormat
import wing.tree.bionda.theme.SunOrange
import wing.tree.bionda.theme.WaterBlue
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.compose.composable.core.VerticalSpacer
import wing.tree.bionda.view.state.MidLandFcstTaState
import java.util.Locale
import wing.tree.bionda.data.model.weather.MidLandFcst.Local as MidLandFcst
import wing.tree.bionda.data.model.weather.MidTa.Local as MidTa

private val weekdays = DateFormatSymbols
    .getInstance(Locale.KOREA)
    .weekdays.filterNot {
        it.isBlank()
    }

@Composable
fun MidLandFcstTa(
    state: MidLandFcstTaState,
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
        when(it) {
            MidLandFcstTaState.Loading -> Loading(modifier = Modifier)
            is MidLandFcstTaState.Content -> Content(
                content = it,
                modifier = Modifier.fillMaxSize()
            )

            is MidLandFcstTaState.Error -> Text("${it.throwable}")
        }
    }
}

@Composable
private fun Content(
    content: MidLandFcstTaState.Content,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        when(val midLandFcstTa = content.midLandFcstTa) {
            is BothSuccess -> BothSuccess(bothSuccess = midLandFcstTa)
            is OneOfSuccess -> OneOfSuccess(oneOfSuccess = midLandFcstTa)
            is BothFailure -> BothFailure(bothFailure = midLandFcstTa)
        }
    }
}

@Composable
private fun BothSuccess(
    bothSuccess: BothSuccess,
    modifier: Modifier = Modifier
) {
    val items = with(bothSuccess) {
        koreaCalendar()
            .julianDay
            .minus(tmFcCalendar.julianDay)
            .let {
                items.drop(it)
            }
    }

    bothSuccess.items
    val (maxTa, minTa) = with(bothSuccess.midTa) {
        maxTa to minTa
    }

    LazyRow(modifier = modifier) {
        items(items) { item ->
            val (n, landFcst, ta) = item

            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TmFc(n = n)
                LandFcst(landFcst = landFcst)
                Ta(
                    ta = ta,
                    max = maxTa.max,
                    min = minTa.min
                )
            }
        }
    }
}

@Composable
private fun OneOfSuccess(
    oneOfSuccess: OneOfSuccess,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when(oneOfSuccess) {
            is OneOfSuccess.MidLandFcst -> {
                val items = with(oneOfSuccess) {
                    val n = koreaCalendar()
                        .julianDay
                        .minus(tmFcCalendar.julianDay)

                    midLandFcst.landFcst.drop(n)
                }

                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(items) {
                        LandFcst(landFcst = it)
                    }
                }
            }

            is OneOfSuccess.MidTa -> {
                val items = with(oneOfSuccess) {
                    val n = koreaCalendar()
                        .julianDay
                        .minus(tmFcCalendar.julianDay)

                    midTa.ta.drop(n)
                }

                val (maxTa, minTa) = with(oneOfSuccess.midTa) {
                    maxTa to minTa
                }

                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(items) {
                        Ta(
                            ta = it,
                            max = maxTa.max,
                            min = minTa.min
                        )
                    }
                }
            }
        }

        Text(text = oneOfSuccess.throwable.message ?: "${oneOfSuccess.throwable}")
    }
}

@Composable
private fun BothFailure(
    bothFailure: BothFailure,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun TmFc(
    n: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val (weekDay, dayOfMonth) = koreaCalendar {
            date += n
        }.let {
            weekdays[it.dayOfWeek.dec()] to dayOfMonthFormat.format(it)
        }

        Text(text = weekDay)
        Text(text = dayOfMonth)
    }
}

@Composable
private fun LandFcst(
    landFcst: MidLandFcst.LandFcst,
    modifier: Modifier = Modifier
) {
    with(landFcst) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.Center
            ) {
                wfAm?.let {
                    Text(text = it)
                }

                wfPm?.let {
                    Text(text = it)
                }

                if (wfAm.isNull()) {
                    wf?.let {
                        Text(text = it)
                    }
                }
            }

            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.Center
            ) {
                rnStAm?.let {
                    Text(text = "$it")
                }

                rnStPm?.let {
                    Text(text = "$it")
                }

                if (rnStAm.isNull()) {
                    rnSt?.let {
                        Text(text = "$it")
                    }
                }
            }
        }
    }
}

@Composable
private fun Ta(
    ta: MidTa.Ta,
    max: Int,
    min: Int,
    modifier: Modifier = Modifier
) {
    // TODO chart처럼 style 정의 필요
    val height = 64.dp // todo remove
    val quantumStep = height.div(max.minus(min).float)
    val topOffset = quantumStep.times(max.minus(ta.max))
    val barHeight = quantumStep.times(ta.max.minus(ta.min))

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpacer(height = topOffset)
        Text(text = ta.max.toString())
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(barHeight)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(SunOrange, Color.Green.copy(alpha = 0.5F), WaterBlue)
                    ),
                    shape = CircleShape
                )
        )

        Text(text = ta.min.toString())
    }
}
