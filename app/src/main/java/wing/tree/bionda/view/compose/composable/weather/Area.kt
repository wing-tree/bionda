package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.core.isSuccess
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.isNotNanOrBlank
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.state.WeatherState.Action

@Composable
fun Area(
    area: State<Area>,
    onAction: (Action.Area) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = area,
        modifier = modifier,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = String.empty,
        contentKey = {
            if (it.isSuccess()) {
                it.value.name
            } else {
                it::class.qualifiedName
            }
        }
    ) {
        when (it) {
            State.Loading -> Loading(modifier = Modifier.fillMaxSize())
            is Complete -> when (it) {
                is Complete.Success -> Content(
                    value = it.value,
                    onAction = onAction,
                    modifier = Modifier.fillMaxSize()
                )

                is Complete.Failure -> {}
            }
        }
    }
}

@Composable
private fun Content(
    value: Area,
    onAction: (Action.Area) -> Unit,
    modifier: Modifier = Modifier
) {
    val text = with(value) {
        when {
            level3.isNotNanOrBlank() `is` true -> level3
            level2.isNotNanOrBlank() `is` true -> level2
            level1.isNotNanOrBlank() `is` true -> level1
            else -> null
        }
    }

    if (text.isNotNull()) {
        Row(
            modifier = modifier.clickable {
                onAction(Action.Area.Click)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onAction(Action.Area.Favorite(value))
                }
            ) {
                val tint = if (value.favorited) {
                    colorScheme.primary
                } else {
                    LocalContentColor.current
                }

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = tint
                )
            }

            Text(text = text)
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null
            )
        }
    }
}
