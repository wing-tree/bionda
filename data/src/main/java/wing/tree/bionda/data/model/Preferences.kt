package wing.tree.bionda.data.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.Preferences.Key
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import wing.tree.bionda.data.model.Preferences as Model
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed class Preferences<T> : () -> Flow<T> {
    abstract val key: Key<T>
    abstract val preferences: DataStore<Preferences>

    class Favorites(override val preferences: DataStore<Preferences>) : Model<Set<String>>() {
        override val key: Key<Set<String>> = stringSetPreferencesKey("favorites")

        override fun invoke(): Flow<Set<String>> {
            return preferences.data.map {
                it[key] ?: emptySet()
            }
        }

        suspend fun toggle(element: String) {
            preferences.edit {
                val elements = it[key]?.toMutableSet() ?: mutableSetOf()

                if (elements.remove(element).not()) {
                    elements.add(element)
                }

                it[key] = elements
            }
        }
    }
}
