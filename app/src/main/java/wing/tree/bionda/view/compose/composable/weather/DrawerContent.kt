package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import wing.tree.bionda.R
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.view.compose.composable.core.Loading
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
        Text(stringResource(id = R.string.favorites), modifier = Modifier.padding(16.dp))
        HorizontalDivider()
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
                    is Complete.Success -> Column {
                        targetState.value.forEach {
                            NavigationDrawerItem(
                                label = {
                                    Text(text = it.name)
                                },
                                selected = false,
                                onClick = {
                                    onAction(WeatherState.Action.Area.Select(it))
                                }
                            )
                        }
                    }

                    is Complete.Failure -> {}
                }
            }
        }
    }
}
