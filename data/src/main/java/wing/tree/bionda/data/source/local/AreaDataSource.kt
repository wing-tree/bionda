package wing.tree.bionda.data.source.local

import android.location.Location
import wing.tree.bionda.data.core.LatLon
import wing.tree.bionda.data.database.dao.AreaDao
import wing.tree.bionda.data.extension.haversine
import wing.tree.bionda.data.model.Area

class AreaDataSource(
    private val dao: AreaDao
) {
    private var loaded: List<Area>? = null

    suspend fun getAreaNo(location: Location): String {
        val loaded = loaded ?: dao.load().also {
            loaded = it
        }

        return loaded.minBy {
            location.haversine(LatLon(lat = it.latitude, lon = it.longitude))
        }
            .no
    }
}
