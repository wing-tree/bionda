package wing.tree.bionda.data.provider

import android.location.Location
import wing.tree.bionda.data.source.local.AreaDataSource

class AreaNoProvider(private val dataSource: AreaDataSource) {
    suspend fun provide(location: Location) = dataSource.nearestArea(location).no
}
