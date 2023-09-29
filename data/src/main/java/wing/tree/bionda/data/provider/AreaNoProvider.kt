package wing.tree.bionda.data.provider

import android.location.Location
import wing.tree.bionda.data.core.LatLon
import wing.tree.bionda.data.extension.haversine
import wing.tree.bionda.data.source.local.AreaDataSource

class AreaNoProvider(private val dataSource: AreaDataSource) {
    suspend fun provide(location: Location) = dataSource.load().minBy {
        location.haversine(LatLon(lat = it.latitude, lon = it.longitude))
    }
        .no
}
