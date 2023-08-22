package wing.tree.bionda.view.compose.composable.weather

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
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.float
import wing.tree.bionda.data.extension.isNull
import wing.tree.bionda.data.model.MidLandFcstTa.BothFailure
import wing.tree.bionda.data.model.MidLandFcstTa.BothSuccess
import wing.tree.bionda.data.model.MidLandFcstTa.OneOfSuccess
import wing.tree.bionda.theme.SunOrange
import wing.tree.bionda.theme.WaterBlue
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.compose.composable.core.VerticalSpacer
import wing.tree.bionda.view.state.MidLandFcstTaState
import wing.tree.bionda.data.model.MidLandFcst.Local as MidLandFcst
import wing.tree.bionda.data.model.MidTa.Local as MidTa

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
        with(content.midLandFcstTa) {
            when(this) {
                is BothSuccess -> BothSuccess(bothSuccess = this)
                is OneOfSuccess -> OneOfSuccess(oneOfSuccess = this)
                is BothFailure -> BothFailure(bothFailure = this)
            }
        }
    }
}

@Composable
private fun BothSuccess(
    bothSuccess: BothSuccess,
    modifier: Modifier = Modifier
) {
    val items = bothSuccess.items
    val (maxTa, minTa) = with(bothSuccess.midTa) {
        maxTa to minTa
    }

    Text(text = bothSuccess.tmFc)

    LazyRow(modifier = modifier) {
        items(items) {
            val (landFcst, ta) = it

            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                val items = oneOfSuccess.midLandFcst.landFcst

                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(items) {
                        LandFcst(landFcst = it)
                    }
                }
            }

            is OneOfSuccess.MidTa -> {
                val items = oneOfSuccess.midTa.ta
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
