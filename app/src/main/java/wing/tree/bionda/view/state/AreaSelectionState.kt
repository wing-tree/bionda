package wing.tree.bionda.view.state

import kotlinx.collections.immutable.ImmutableList
import wing.tree.bionda.data.extension.negativeOne
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.model.Level

data class AreaSelectionState(
    val areaState: AreaState,
    val level: Level
) {
    companion object {
        val initialValue = AreaSelectionState(
            areaState = AreaState.initialValue,
            level = Level()
        )
    }
}

sealed interface AreaState {
    val contentKey: String?
    val isLevel1: Boolean get() = this is Content.Level1
    val isNotLevel1: Boolean get() = isLevel1.not()

    object Loading : AreaState {
        override val contentKey: String?
            get() = javaClass.canonicalName
    }

    sealed class Content : AreaState, Comparable<Content> {
        abstract val items: ImmutableList<*>

        override val contentKey: String? = javaClass.enclosingClass.canonicalName

        override fun compareTo(other: Content): Int {
            return when (this) {
                is Level1 -> when (other) {
                    is Level1 -> Int.zero
                    else -> Int.negativeOne
                }

                is Level2 -> when (other) {
                    is Level1 -> Int.one
                    is Level2 -> Int.zero
                    is Level3 -> Int.negativeOne
                }

                is Level3 -> when (other) {
                    is Level3 -> Int.zero
                    else -> Int.one
                }
            }
        }

        data class Level1(override val items: ImmutableList<String>) : Content()
        data class Level2(override val items: ImmutableList<String>) : Content()
        data class Level3(override val items: ImmutableList<Area>) : Content()
    }

    data class Error(val exception: Throwable) : AreaState {
        override val contentKey: String?
            get() = javaClass.canonicalName
    }

    sealed interface Action {
        sealed interface Click : Action {
            data class Level1(val value: String) : Click
            data class Level2(val value: String) : Click
            data class Level3(val value: Area) : Click
        }
    }

    companion object {
        val initialValue = Loading
    }
}
