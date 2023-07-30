package wing.tree.bionda.view.compose.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wing.tree.bionda.data.extension.COLON
import wing.tree.bionda.data.extension.EMPTY
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.view.state.NoticeState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Notice(
    state: NoticeState,
    onCheckedChange: (Notice, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state,
        modifier = modifier,
        transitionSpec = {
            fadeIn() with fadeOut()
        },
        label = String.EMPTY
    ) {
        when (it) {
            NoticeState.Loading -> Loading(modifier = Modifier.fillMaxSize())
            is NoticeState.Content -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(it.notices) { item ->
                    Item(
                        item = item,
                        onCheckedChange = onCheckedChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            is NoticeState.Error -> {}
        }
    }
}

@Composable
private fun Item(
    item: Notice,
    onCheckedChange: (Notice, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        val text = "${item.hour}${String.COLON}${item.minute}"

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var checked by remember(key1 = item.checked) {
                mutableStateOf(item.checked)
            }

            Text(
                text = text,
                style = typography.titleLarge
            )

            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    onCheckedChange(item, checked)
                }
            )
        }
    }
}
