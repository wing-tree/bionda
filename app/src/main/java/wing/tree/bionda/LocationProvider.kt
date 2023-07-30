package wing.tree.bionda

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import wing.tree.bionda.data.extension.ZERO
import wing.tree.bionda.data.extension.isNull
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.exception.OnCanceledException
import kotlin.coroutines.resume

class LocationProvider(private val context: Context)  {
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private suspend fun getLocationSettingsResponse(): Complete<LocationSettingsResponse> {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            Long.ZERO
        )
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .build()

        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        val settingsClient = LocationServices.getSettingsClient(context)

        return suspendCancellableCoroutine {
            settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnCanceledListener {
                    it.resume(Complete.Failure(OnCanceledException))
                }.addOnFailureListener { exception ->
                    it.resume(Complete.Failure(exception))
                }.addOnSuccessListener { locationSettingsResponse ->
                    it.resume(Complete.Success(locationSettingsResponse))
                }
        }
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    suspend fun getLocation(): Complete<Location?> = when (val currentLocation = getCurrentLocation()) {
        is Complete.Success -> if (currentLocation.isNull()) {
            getLastLocation()
        } else {
            currentLocation
        }

        is Complete.Failure -> getLastLocation()
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    suspend fun getCurrentLocation() : Complete<Location?> {
        return when (val locationSettingsResponse = getLocationSettingsResponse()) {
            is Complete.Failure -> locationSettingsResponse
            is Complete.Success -> suspendCancellableCoroutine {
                fusedLocationProviderClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    null
                )
                    .addOnCanceledListener {
                        it.resume(Complete.Failure(OnCanceledException))
                    }
                    .addOnFailureListener { exception ->
                        it.resume(Complete.Failure(exception))
                    }
                    .addOnSuccessListener { location ->
                        it.resume(Complete.Success(location))
                    }
            }
        }
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    suspend fun getLastLocation() : Complete<Location?> {
        return when (val locationSettingsResponse = getLocationSettingsResponse()) {
            is Complete.Failure -> locationSettingsResponse
            is Complete.Success -> suspendCancellableCoroutine {
                fusedLocationProviderClient.lastLocation
                    .addOnCanceledListener {
                        it.resume(Complete.Failure(OnCanceledException))
                    }
                    .addOnFailureListener { exception ->
                        it.resume(Complete.Failure(exception))
                    }
                    .addOnSuccessListener { location ->
                        it.resume(Complete.Success(location))
                    }
            }
        }
    }

    private fun Complete.Success<Any?>.isNull() = data.isNull()
}
