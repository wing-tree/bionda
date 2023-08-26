package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.model.State
import wing.tree.bionda.data.model.State.Complete
import wing.tree.bionda.model.VilageFcst
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.compose.composable.core.VerticalSpacer

@Composable
fun VilageFcst(
    state: State<VilageFcst>,
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
            State.Loading -> Loading(modifier = Modifier)
            is Complete -> when (it) {
                is Complete.Success -> Content(
                    vilageFcst = it.value,
                    modifier = Modifier.fillMaxWidth()
                )

                is Complete.Failure -> Text(text = "${it.throwable}")
            }
        }
    }
}

@Composable
private fun Content(
    vilageFcst: VilageFcst,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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
