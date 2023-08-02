package wing.tree.bionda.view.compose.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.extension.COLON
import wing.tree.bionda.data.extension.EMPTY
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.view.state.NoticeState

@Composable
fun Notice(
    state: NoticeState,
    onClick: (Notice) -> Unit,
    onLongClick: (Notice) -> Unit,
    onCheckedChange: (Notice, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state,
        modifier = modifier,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = String.EMPTY,
        contentKey = {
            it::class.qualifiedName
        }
    ) {
        when (it) {
            NoticeState.Loading -> Loading(modifier = Modifier.fillMaxSize())
            is NoticeState.Content -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(it.notices) { item ->
                    Item(
                        item = item,
                        onClick = onClick,
                        onLongClick = onLongClick,
                        onCheckedChange = onCheckedChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            is NoticeState.Error -> {}
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Item(
    item: Notice,
    onClick: (Notice) -> Unit,
    onLongClick: (Notice) -> Unit,
    onCheckedChange: (Notice, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    onClick(item)
                },
                onLongClick = {
                    onLongClick(item)
                },
        ),
    ) {
        val text = "${item.hour}${String.COLON}${item.minute}"

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
