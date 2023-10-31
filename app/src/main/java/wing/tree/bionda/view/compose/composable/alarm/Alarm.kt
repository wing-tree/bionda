package wing.tree.bionda.view.compose.composable.alarm

import android.icu.text.SimpleDateFormat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.primaryContainerColor
import androidx.compose.material3.TabRowDefaults.primaryContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import wing.tree.bionda.R
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.full
import wing.tree.bionda.data.model.Alarm
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.model.WindowSizeClass
import wing.tree.bionda.view.compose.composable.core.Empty
import wing.tree.bionda.view.compose.composable.core.HorizontalSpacer
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.state.AlarmState
import wing.tree.bionda.view.state.AlarmState.Action
import java.util.Locale

private val simpleDateFormat = SimpleDateFormat("a h:mm", Locale.KOREA)

@Composable
fun Alarm(
    state: AlarmState,
    inSelectionMode: Boolean,
    onAction: (Action) -> Unit,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(windowSizeClass.marginValues)
        ) {
            RequestPermissions(
                requestPermissions = state.requestPermissions,
                onAction = {
                    onAction(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        onAction(Action.Add)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_alarm_24),
                        contentDescription = null
                    )
                }
            }

            Alarms(
                state = state,
                inSelectionMode = inSelectionMode,
                onAction = {
                    onAction(it)
                },
                modifier = Modifier.weight(Float.full)
            )
        }

        SelectionMode(
            inSelectionMode = inSelectionMode,
            onAction = onAction,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun SelectionMode(
    inSelectionMode: Boolean,
    onAction: (Action.SelectionMode) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = inSelectionMode,
        modifier = modifier,
        enter = slideInVertically {
            it
        },
        exit = slideOutVertically {
            it
        }
    ) {
        Surface(
            color = primaryContainerColor,
            contentColor = primaryContentColor
        ) {
            Row {
                Tab(
                    selected = false,
                    onClick = {
                        onAction(Action.SelectionMode.ALARM_ON)
                    },
                    modifier = Modifier.weight(Float.full),
                    text = {
                        Text(text = stringResource(id = R.string.alarm_on))
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_alarm_on_24),
                            contentDescription = null
                        )
                    }
                )

                Tab(
                    selected = false,
                    onClick = {
                        onAction(Action.SelectionMode.ALARM_OFF)
                    },
                    modifier = Modifier.weight(Float.full),
                    text = {
                        Text(text = stringResource(id = R.string.alarm_off))
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_alarm_off_24),
                            contentDescription = null
                        )
                    }
                )

                Tab(
                    selected = false,
                    onClick = {
                        onAction(Action.SelectionMode.DELETE_ALL)
                    },
                    modifier = Modifier.weight(Float.full),
                    text = {
                        Text(text = stringResource(id = R.string.delete))
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun Alarms(
    state: AlarmState,
    inSelectionMode: Boolean,
    onAction: (Action) -> Unit,
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
            is AlarmState.Loading -> Loading(modifier = Modifier.fillMaxSize())
            is AlarmState.Content -> Content(
                content = it,
                inSelectionMode = inSelectionMode,
                onAction = onAction,
                modifier = Modifier.fillMaxSize()
            )

            is AlarmState.Error -> {}
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(
    content: AlarmState.Content,
    inSelectionMode: Boolean,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    val alarms = content.alarms
    
    Crossfade(
        targetState = alarms.isNotEmpty(),
        modifier = modifier.padding(bottom = 72.dp),
        label = String.empty
    ) {
        if (it) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(
                    items = content.alarms,
                    key = { alarm ->
                        alarm.id
                    }
                ) { item ->
                    Item(
                        item = item,
                        inSelectionMode = inSelectionMode,
                        selected = item.id in content.selected,
                        onAction = onAction,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement()
                    )
                }
            }
        } else {
            Empty(
                text = stringResource(id = R.string.no_alarms),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Item(
    item: Alarm,
    inSelectionMode: Boolean,
    selected: Boolean,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        val text = simpleDateFormat.format(koreaCalendar(item.hour, item.minute))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onAction(Action.Alarms.Click(item))
                    },
                    onLongClick = {
                        onAction(Action.Alarms.LongClick(item))
                    }
                )
                .padding(
                    start = 16.dp,
                    top = 12.dp,
                    end = 24.dp,
                    bottom = 12.dp
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
                        onAction(Action.Alarms.SelectedChange(item, it))
                    },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(24.dp)
                )
            }

            Text(
                text = text,
                modifier = Modifier.weight(Float.full),
                style = MaterialTheme.typography.titleMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Alarm.Condition.entries.forEach {
                    val alpha = if (it in item.conditions) {
                        1.0F
                    } else {
                        0.38F
                    }

                    Text(
                        text = when (it) {
                            Alarm.Condition.RAIN -> stringResource(id = R.string.rain)
                            Alarm.Condition.SNOW -> stringResource(id = R.string.snow)
                        },
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = rememberRipple(bounded = false)
                            ) {
                                onAction(Action.Alarms.ConditionClick(item, it))
                            }
                            .alpha(alpha),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            HorizontalSpacer(width = 16.dp)

            Switch(
                checked = on,
                onCheckedChange = {
                    on = it
                    onAction(Action.Alarms.CheckChange(item, on))
                }
            )
        }
    }
}
