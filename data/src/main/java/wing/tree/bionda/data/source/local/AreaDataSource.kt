package wing.tree.bionda.data.source.local

import android.location.Location
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import wing.tree.bionda.data.core.LatLon
import wing.tree.bionda.data.database.dao.AreaDao
import wing.tree.bionda.data.extension.haversine
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.data.model.Preferences.Favorites

class AreaDataSource(
    private val dao: AreaDao,
    dataStore: DataStore<Preferences>
) {
    private var loaded: List<Area>? = null

    val favorites = Favorites(dataStore)

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
}
