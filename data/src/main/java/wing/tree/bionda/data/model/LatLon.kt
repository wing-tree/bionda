package wing.tree.bionda.data.model

import wing.tree.bionda.data.extension.delta
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.radians

data class LatLon(val lat: Double, val lon: Double) {
    fun delta(latitude: Double, longitude: Double): LatLon {
        return LatLon(lat.delta(latitude), lon.delta(longitude))
    }

    fun half() = copy(lat = lat.half, lon = lon.half)
    fun radians() = copy(lat = lat.radians, lon = lon.radians)
}
