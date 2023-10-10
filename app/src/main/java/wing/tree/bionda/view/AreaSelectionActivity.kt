package wing.tree.bionda.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import wing.tree.bionda.data.constant.EXTRA_AREA
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.isNonNegative
import wing.tree.bionda.data.extension.negated
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.theme.BiondaTheme
import wing.tree.bionda.view.compose.composable.core.Loading
import wing.tree.bionda.view.model.AreaSelectionViewModel
import wing.tree.bionda.view.state.AreaState
import wing.tree.bionda.view.state.AreaState.Action
import wing.tree.bionda.view.state.AreaState.Action.Click

@AndroidEntryPoint
class AreaSelectionActivity : ComponentActivity() {
    private val viewModel by viewModels<AreaSelectionViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BiondaTheme {
                var active by rememberSaveable {
                    mutableStateOf(false)
                }

                Scaffold(
                    topBar = {
                        val query by viewModel.query.collectAsStateWithLifecycle()
                        val searched by viewModel.searched.collectAsStateWithLifecycle()

                        CompositionLocalProvider {
                            ProvideTextStyle(value = typography.bodyLarge.copy(fontFamily = FontFamily.Default)) {
                                SearchBar(
                                    query = query,
                                    onQueryChange = viewModel::search,
                                    onSearch = viewModel::search,
                                    active = active,
                                    onActiveChange = {
                                        active = it
                                    },
                                    leadingIcon = {
                                        IconButton(
                                            onClick = {
                                                if (active) {
                                                    active = false
                                                } else {
                                                    finish()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    trailingIcon = {
                                        AnimatedVisibility(
                                            visible = query.isNotBlank(),
                                            enter = fadeIn(),
                                            exit = fadeOut()
                                        ) {
                                            IconButton(
                                                onClick = viewModel::clear
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Crossfade(targetState = searched, label = String.empty) {
                                        LazyColumn {
                                            items(
                                                items = it,
                                                key = Area::name
                                            ) { item ->
                                                ListItem(
                                                    headlineContent = {
                                                        val text = buildAnnotatedString {
                                                            val index = item.name.indexOf(query)

                                                            if (index.isNonNegative) {
                                                                addStyle(
                                                                    style = SpanStyle(color = colorScheme.primary),
                                                                    start = index,
                                                                    end = index.plus(query.length)
                                                                )
                                                            }

                                                            append(item.name)
                                                        }

                                                        Text(text = text)
                                                    },
                                                    modifier = Modifier.clickable {
                                                        setResult(
                                                            RESULT_OK,
                                                            Intent().putExtra(EXTRA_AREA, item)
                                                        )

                                                        finish()
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    BackHandler(active || state.areaState.isNotLevel1) {
                        if (active) {
                            active = false
                        } else {
                            viewModel.levelDown()
                        }
                    }

                    AnimatedContent(
                        targetState = state.areaState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = String.empty,
                        contentKey = AreaState::contentKey
                    ) { targetState ->
                        when (targetState) {
                            AreaState.Loading -> Loading(modifier = Modifier.fillMaxSize())
                            is AreaState.Content -> Content(
                                content = targetState,
                                onAction = { action ->
                                    when (action) {
                                        is Click -> when (action) {
                                            is Click.Level1 -> viewModel.levelUp(action.value)
                                            is Click.Level2 -> viewModel.levelUp(action.value)
                                            is Click.Level3 -> {
                                                setResult(
                                                    RESULT_OK,
                                                    Intent().putExtra(EXTRA_AREA, action.value)
                                                )

                                                finish()
                                            }
                                        }
                                    }
                                }
                            )

                            is AreaState.Error -> Text(
                                text = targetState.exception.message ?: "unknown"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Content(
    content: AreaState.Content,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = content,
        modifier = modifier,
        transitionSpec = {
            when {
                initialState < targetState -> slideInHorizontally {
                    it
                } togetherWith slideOutHorizontally {
                    it.negated
                }

                initialState > targetState -> slideInHorizontally {
                    it.negated
                } togetherWith slideOutHorizontally {
                    it
                }

                else -> fadeIn() togetherWith fadeOut()
            }
        },
        label = String.empty
    ) {
        when (it) {
            is AreaState.Content.Level1 -> Level1(
                level1 = it,
                onItemClick = { item ->
                    onAction(Click.Level1(item))
                }
            )

            is AreaState.Content.Level2 -> Level2(
                level2 = it,
                onItemClick = { item ->
                    onAction(Click.Level2(item))
                }
            )

            is AreaState.Content.Level3 -> Level3(
                level3 = it,
                onItemClick = { item ->
                    onAction(Click.Level3(item))
                }
            )
        }
    }
}

@Composable
private fun Level1(
    level1: AreaState.Content.Level1,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(level1.items) { item ->
            ListItem(
                headlineContent = {
                    Text(text = item)
                },
                modifier = Modifier.clickable {
                    onItemClick(item)
                }
            )
        }
    }
}

@Composable
private fun Level2(
    level2: AreaState.Content.Level2,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(level2.items) { item ->
            ListItem(
                headlineContent = {
                    Text(text = item)
                },
                modifier = Modifier.clickable {
                    onItemClick(item)
                }
            )
        }
    }
}

@Composable
private fun Level3(
    level3: AreaState.Content.Level3,
    onItemClick: (Area) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(level3.items) { item ->
            ListItem(
                headlineContent = {
                    Text(text = item.name)
                },
                modifier = Modifier.clickable {
                    onItemClick(item)
                }
            )
        }
    }
}
