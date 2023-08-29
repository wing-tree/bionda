package wing.tree.bionda.extension

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import wing.tree.bionda.data.extension.double
import wing.tree.bionda.data.extension.half
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.quarter
import wing.tree.bionda.data.extension.ten
import wing.tree.bionda.data.core.Address
import wing.tree.bionda.model.Coordinate
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

private const val RE = 6371.00877
private const val GRID = 5.0
private const val DEGRAD = PI / 180.0
private const val SLAT1 = 30.0 * DEGRAD
private const val SLAT2 = 60.0 * DEGRAD
private const val OLON = 126.0 * DEGRAD
private const val OLAT = 38.0 * DEGRAD
private const val XO = 43.0
private const val YO = 136.0

private val sn = ln(cos(SLAT1) / cos(SLAT2)) / ln(tan(PI.quarter.plus(SLAT2.half)) / tan(PI.quarter + SLAT1.half))
private val sf = tan(PI.quarter + SLAT1.half).pow(sn) * cos(SLAT1) / sn
private val ro = RE / GRID * sf / tan(PI.quarter + OLAT.half).pow(sn)

fun Location.toCoordinate(): Coordinate {
    val lon = longitude
    val lat = latitude

    val ra = RE / GRID * sf / tan(PI.quarter + lat * DEGRAD.half).pow(sn)

    var theta = lon * DEGRAD - OLON

    theta = when {
        theta < -PI -> theta + PI.double
        theta > PI -> theta - PI.double
        else -> theta
    }

    return Coordinate(
        nx = floor(ra * sin(theta * sn) + XO + 0.5).int,
        ny = floor(ro - ra * cos(theta * sn) + YO + 0.5).int
    )
}

suspend fun Location.getAddress(context: Context) = suspendCancellableCoroutine { cancellableContinuation ->
    val geocoder = Geocoder(context, Locale.KOREA)
    val maxResults = Int.ten

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(latitude, longitude, maxResults) { geocode ->
            cancellableContinuation.resume(
                Address.get(geocode.filterNotNull())
            )
        }
    } else {
        @Suppress("DEPRECATION")
        geocoder.getFromLocation(latitude, longitude, maxResults)?.let { geocode ->
            cancellableContinuation.resume(
                Address.get(geocode.filterNotNull())
            )
        } ?: cancellableContinuation.resume(null)
    }
}
