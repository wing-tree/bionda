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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.extension.date
import wing.tree.bionda.data.extension.dayOfWeek
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.float
import wing.tree.bionda.data.extension.isNull
import wing.tree.bionda.data.extension.julianDay
import wing.tree.bionda.data.model.core.State
import wing.tree.bionda.data.model.core.State.Complete
import wing.tree.bionda.data.model.MidLandFcstTa
import wing.tree.bionda.data.model.MidLandFcstTa.BothFailure
import wing.tree.bionda.data.model.MidLandFcstTa.BothSuccess
import wing.tree.bionda.data.model.MidLandFcstTa.OneOfSuccess
import wing.tree.bionda.data.top.level.dayOfMonthFormat
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.model.WeatherIcons
import wing.tree.bionda.theme.SunOrange
import wing.tree.bionda.theme.WaterBlue
import wing.tree.bionda.view.compose.composable.core.DegreeText
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.compose.composable.core.VerticalSpacer
import java.util.Locale
import wing.tree.bionda.data.model.MidLandFcst.Local as MidLandFcst
import wing.tree.bionda.data.model.MidTa.Local as MidTa

private val weekdays = DateFormatSymbols
    .getInstance(Locale.KOREA)
    .weekdays.filterNot {
        it.isBlank()
    }

@Composable
fun MidLandFcstTa(
    state: State<MidLandFcstTa>,
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
            State.Loading -> Loading(modifier = Modifier)
            is Complete -> when (it) {
                is Complete.Success -> Content(content = it.value)
                is Complete.Failure -> Text("${it.throwable}")
            }
        }
    }
}

@Composable
private fun Content(
    content: MidLandFcstTa,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            when(content) {
                is BothSuccess -> BothSuccess(bothSuccess = content)
                is OneOfSuccess -> OneOfSuccess(oneOfSuccess = content)
                is BothFailure -> BothFailure(bothFailure = content)
            }
        }
    }
}

@Composable
private fun BothSuccess(
    bothSuccess: BothSuccess,
    modifier: Modifier = Modifier
) {
    val items = with(bothSuccess) {
        val n = koreaCalendar
            .julianDay
            .minus(julianDay)

        advancedDayBy(n)
    }

    val (maxTa, minTa) = with(bothSuccess.midTa) {
        maxTa to minTa
    }

    // TODO: Remove
    Text(text = bothSuccess.tmFc)

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
                    val n = koreaCalendar
                        .julianDay
                        .minus(julianDay)

                    midLandFcst.advancedDayBy(n)
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items) {
                        Column {
                            TmFc(n = it.n)
                            LandFcst(landFcst = it)
                        }
                    }
                }
            }

            is OneOfSuccess.MidTa -> {
                val items = with(oneOfSuccess) {
                    val n = koreaCalendar
                        .julianDay
                        .minus(julianDay)

                    midTa.advancedDayBy(n)
                }

                val (maxTa, minTa) = with(oneOfSuccess.midTa) {
                    maxTa to minTa
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items) {
                        Column {
                            TmFc(n = it.n)
                            Ta(
                                ta = it,
                                max = maxTa.max,
                                min = minTa.min
                            )
                        }
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
    Column(modifier = modifier) {
        with(bothFailure) {
            Text(text = midLandFcst.message ?: "$midLandFcst")
            Text(text = midTa.message ?: "$midTa")
        }
    }
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
                val weatherIcons = remember {
                    WeatherIcons.Daytime
                }

                weatherIcons.wf[wfAm]?.let {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                }

                weatherIcons.wf[wfPm]?.let {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                }

                if (wfAm.isNull()) {
                    weatherIcons.wf[wf]?.let {
                        Icon(
                            painter = painterResource(id = it),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
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
        DegreeText(text = "${ta.max}")
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

        DegreeText(text = "${ta.min}")
    }
}
