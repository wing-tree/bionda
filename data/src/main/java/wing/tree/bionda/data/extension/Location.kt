package wing.tree.bionda.data.extension

import android.location.Location
import wing.tree.bionda.data.core.LatLon
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun Location.haversine(latLon: LatLon): Double {
    val delta = latLon
        .delta(latitude, longitude)
        .radians()
        .half()

    val a = sin(delta.lat).pow(Int.two) +
            cos(latitude.radians) *
            cos(latLon.lon.radians) *
            sin(delta.lon).pow(Int.two)

    val c = atan2(sqrt(a), sqrt(Double.one.minus(a))).double

    return 6371.times(c)
}
