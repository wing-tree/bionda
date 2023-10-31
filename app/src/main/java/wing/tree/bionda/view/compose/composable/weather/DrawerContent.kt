package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import wing.tree.bionda.R
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.top.level.rememberMutableInteractionSource
import wing.tree.bionda.view.compose.composable.core.Empty
import wing.tree.bionda.view.compose.composable.core.Error
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.compose.composable.core.Style
import wing.tree.bionda.view.state.DrawerContentState
import wing.tree.bionda.view.state.WeatherState

@Composable
fun DrawerContent(
    state: DrawerContentState,
    onAction: (WeatherState.Action) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        Favorites(
            state = state.favorites,
            onAction = onAction
        )
    }
}

@Composable
private fun Favorites(
    state: State<PersistentList<Area>>,
    onAction: (WeatherState.Action) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.favorites),
            modifier = Modifier.padding(16.dp),
            style = typography.titleSmall
        )

        AnimatedContent(
            targetState = state,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = String.empty,
            contentKey = {
                it::class.qualifiedName
            }
        ) { targetState ->
            when (targetState) {
                State.Loading -> Loading()
                is Complete -> when (targetState) {
                    is Complete.Success -> Content(
                        content = targetState,
                        onAction = onAction
                    )

                    is Complete.Failure -> Error(targetState.exception)
                }
            }
        }
    }
}

@Composable
private fun Content(
    content: Complete.Success<PersistentList<Area>>,
    onAction: (WeatherState.Action) -> Unit,
    modifier: Modifier = Modifier
) {
    val value = content.value

    Crossfade(targetState = value.isNotEmpty(), label = String.empty) {
        if (it) {
            Column(modifier = modifier) {
                content.value.forEach {
                    NavigationDrawerItem(
                        label = {
                            Text(text = it.name)
                        },
                        selected = false,
                        onClick = {
                            onAction(WeatherState.Action.Area.Select(it))
                        },
                        icon = {
                            val tint by animateColorAsState(
                                targetValue = if (it.favorited.value) {
                                    colorScheme.primary
                                } else {
                                    LocalContentColor.current
                                },
                                label = String.empty
                            )

                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                modifier = Modifier.clickable(
                                    interactionSource = rememberMutableInteractionSource(),
                                    indication = rememberRipple(bounded = false)
                                ) {
                                    onAction(WeatherState.Action.Area.Favorite(it.no))
                                },
                                tint = tint
                            )
                        }
                    )
                }
            }
        } else {
            Empty(
                text = stringResource(id = R.string.no_favorites),
                modifier = Modifier.fillMaxSize(),
                style = Style.MEDIUM
            )
        }
    }
}
