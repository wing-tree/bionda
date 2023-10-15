package wing.tree.bionda.data.source.local

import android.location.Location
import androidx.datastore.core.DataStore
import wing.tree.bionda.data.core.LatLon
import wing.tree.bionda.data.database.dao.AreaDao
import wing.tree.bionda.data.extension.haversine
import wing.tree.bionda.data.model.Area
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.map

class AreaDataSource(
    private val dao: AreaDao,
    private val dataStore: DataStore<Preferences>
) {
    private val key = stringSetPreferencesKey("favorites")

    private var loaded: List<Area>? = null

    val favorites = dataStore.data.map {
        it[key] ?: emptySet()
    }

    suspend fun load(): List<Area> {
        val loaded = loaded ?: dao.load().also {
            loaded = it
        }

        return loaded
    }

    suspend fun nearestArea(location: Location) = load().minBy {
        location.haversine(LatLon(lat = it.latitude, lon = it.longitude))
    }

    suspend fun update(area: Area) {
        dao.update(area)
    }

    suspend fun update(area: Area, favorited: Boolean) {
        dataStore.edit {
            val favorites = it[key]?.toMutableSet() ?: mutableSetOf()

            it[key] = if (favorited) {
                favorites.plus(area.no)
            } else {
                favorites.minus(area.no)
            }
        }
    }
}
