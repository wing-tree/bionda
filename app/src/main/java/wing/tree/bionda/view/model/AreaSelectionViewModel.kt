package wing.tree.bionda.view.model

import android.app.Application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wing.tree.bionda.data.core.HangulJamo.jamo
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.hundreds
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.isNan
import wing.tree.bionda.data.extension.nan
import wing.tree.bionda.data.extension.seven
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.data.source.local.AreaDataSource
import wing.tree.bionda.data.top.level.emptyPersistentList
import wing.tree.bionda.model.Level
import wing.tree.bionda.view.state.AreaSelectionState
import wing.tree.bionda.view.state.AreaState.Content
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class AreaSelectionViewModel @Inject constructor(
    application: Application,
    areaDataSource: AreaDataSource
) : BaseViewModel(application) {
    private val area = flow {
        emit(areaDataSource.load())
    }
        .stateIn(persistentListOf())

    private val level = MutableStateFlow(Level())
    private val period = Int.seven.hundreds.milliseconds

    private val _query = MutableStateFlow(String.empty)
    val query = _query.asStateFlow()

    @OptIn(FlowPreview::class)
    val searched = combine(area, query.sample(period)) { area, query ->
        if (query.isBlank()) {
            emptyPersistentList()
        } else {
            persistentListOf(
                area.filter {
                    query.jamo in it.name.jamo
                }
            )
                .flatten()
        }
    }
        .stateIn(emptyPersistentList())

    val state: StateFlow<AreaSelectionState> = combine(level, area) { level, area ->
        area.groupBy(Area::level1).run {
            get(level.one) ?: return@combine run {
                AreaSelectionState(
                    areaState = Content.Level1(
                        keys
                            .filterNot(String::isNan)
                            .toPersistentList()
                    ),
                    level = level
                )
            }
        }.groupBy(Area::level2).run {
            get(level.two)?.let {
                Content.Level3(
                    it.filterNot { area ->
                        area.level3.isNan()
                    }
                        .toPersistentList()
                )
            }
                ?: Content.Level2(keys.filterNot { it `is` String.nan }.toPersistentList())
        }.let {
            AreaSelectionState(
                areaState = it,
                level = level
            )
        }
    }
        .stateIn(initialValue = AreaSelectionState.initialValue)

    fun clear() {
        _query.value = String.empty
    }

    fun levelDown() {
        level.update {
            it.levelDown()
        }
    }

    fun levelUp(value: String) {
        level.update {
            it.levelUp(value)
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _query.value = query
        }
    }
}
