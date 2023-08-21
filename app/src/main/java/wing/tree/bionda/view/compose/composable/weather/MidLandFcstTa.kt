package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.isNull
import wing.tree.bionda.data.model.MidLandFcst.Local as MidLandFcst
import wing.tree.bionda.data.model.MidLandFcstTa.BothFailure
import wing.tree.bionda.data.model.MidLandFcstTa.BothSuccess
import wing.tree.bionda.data.model.MidLandFcstTa.OneOfSuccess
import wing.tree.bionda.data.model.MidTa.Local as MidTa
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.state.MidLandFcstTaState

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
    LazyRow(modifier = modifier) {
        items(bothSuccess.items) {
            Item(item = it)
        }
    }
}

@Composable
private fun Item(
    item: Pair<MidLandFcst.Item, MidTa.Ta>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Item(item = item.first)
    }
}

@Composable
private fun OneOfSuccess(
    oneOfSuccess: OneOfSuccess,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun BothFailure(
    bothFailure: BothFailure,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun Item(
    item: MidLandFcst.Item,
    modifier: Modifier = Modifier
) {
    val rnStAm = item.rnStAm
    val rnStPm = item.rnStPm
    val rnSt = item.rnSt
    val wfAm = item.wfAm
    val wfPm = item.wfPm
    val wf = item.wf

    Column {
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
