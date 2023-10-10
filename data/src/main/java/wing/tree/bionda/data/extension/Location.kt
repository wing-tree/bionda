package wing.tree.bionda.data.extension

import android.location.Location
import wing.tree.bionda.data.core.LatLon
import kotlin.math.asin
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
            sin(delta.lon).pow(Int.two) *
            cos(latLon.lat.radians) *
            cos(latitude.radians)

    val c = asin(sqrt(a)).double

    return 6372.8.times(c)
}
