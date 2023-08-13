package wing.tree.bionda.view.compose.composable

import android.icu.text.SimpleDateFormat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.regular.koreaCalendar
import wing.tree.bionda.extension.zero
import wing.tree.bionda.view.state.NoticeState
import java.util.Locale

private val simpleDateFormat = SimpleDateFormat("a h:mm", Locale.KOREA)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Notice(
    state: NoticeState,
    inSelectionMode: Boolean,
    onClick: (Notice) -> Unit,
    onLongClick: (Notice) -> Unit,
    onCheckedChange: (Notice, Boolean) -> Unit,
    onSelectedChange: (Notice, Boolean) -> Unit,
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
            NoticeState.Loading -> Loading(modifier = Modifier.fillMaxSize())
            is NoticeState.Content -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = if (inSelectionMode) {
                        72.dp
                    } else {
                        Dp.zero
                    }
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = it.notices,
                    key = { notice ->
                        notice.id
                    }
                ) { item ->
                    Item(
                        item = item,
                        inSelectionMode = inSelectionMode,
                        selected = item.id in it.selected,
                        onClick = onClick,
                        onLongClick = onLongClick,
                        onCheckedChange = onCheckedChange,
                        onSelectedChange = onSelectedChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement()
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
    inSelectionMode: Boolean,
    selected: Boolean,
    onClick: (Notice) -> Unit,
    onLongClick: (Notice) -> Unit,
    onCheckedChange: (Notice, Boolean) -> Unit,
    onSelectedChange: (Notice, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = {
                    onClick(item)
                },
                onLongClick = {
                    onLongClick(item)
                },
            ),
    ) {
        val text = simpleDateFormat.format(koreaCalendar(item.hour, item.minute))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 24.dp,
                    bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var on by remember(key1 = item.on) {
                mutableStateOf(item.on)
            }

            AnimatedVisibility(visible = inSelectionMode) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = {
                        onSelectedChange(item, it)
                    },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(24.dp)
                )
            }

            Text(
                text = text,
                modifier = Modifier.weight(Float.one),
                style = typography.titleLarge
            )

            Switch(
                checked = on,
                onCheckedChange = {
                    on = it
                    onCheckedChange(item, on)
                }
            )
        }
    }
}
