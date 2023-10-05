package wing.tree.bionda.data.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.Preferences.Key
import kotlinx.coroutines.flow.Flow

sealed interface Preferences : (DataStore<Preferences>) -> Flow<*> {
    val name: String
    val key: Key<*>
}
