package wing.tree.bionda.provider

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import wing.tree.bionda.data.extension.ZERO
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.model.ifFailure
import wing.tree.bionda.data.model.ifNull
import wing.tree.bionda.exception.OnCanceledException
import kotlin.coroutines.resume

class LocationProvider(private val context: Context)  {
    private val currentLocationRequest by lazy {
        CurrentLocationRequest
            .Builder()
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()
    }

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val lastLocationRequest by lazy {
        LastLocationRequest
            .Builder()
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .build()
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    suspend fun getLocation(): Complete<Location?> {
        val currentLocation = getCurrentLocation()
            .ifNull {
                getCurrentLocation(currentLocationRequest)
            }
            .ifFailure {
                Timber.e(it)

                getCurrentLocation(currentLocationRequest)
            }

        return when (currentLocation) {
            is Complete.Success -> currentLocation.ifNull {
                getLastLocation()
                    .ifNull {
                        getLastLocation(lastLocationRequest)
                    }
                    .ifFailure {
                        Timber.e(it)

                        getLastLocation(lastLocationRequest)
                    }
            }
            is Complete.Failure -> {
                Timber.e(currentLocation.throwable)

                getLastLocation()
                    .ifNull {
                        getLastLocation(lastLocationRequest)
                    }
                    .ifFailure {
                        Timber.e(it)

                        getLastLocation(lastLocationRequest)
                    }
            }
        }
    }

    @Suppress("unused")
    private suspend fun getLocationSettingsResponse(): Complete<LocationSettingsResponse> {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            Long.ZERO
        )
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .build()

        val locationSettingsRequest = LocationSettingsRequest
            .Builder()
            .addLocationRequest(locationRequest)
            .build()

        val settingsClient = LocationServices.getSettingsClient(context)

        return suspendCancellableCoroutine {
            settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnCanceledListener {
                    it.resume(Complete.Failure(OnCanceledException))
                }
                .addOnFailureListener { exception ->
                    it.resume(Complete.Failure(exception))
                }
                .addOnSuccessListener { locationSettingsResponse ->
                    it.resume(Complete.Success(locationSettingsResponse))
                }
        }
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    suspend fun getCurrentLocation(): Complete<Location?> {
        return suspendCancellableCoroutine {
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

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    suspend fun getCurrentLocation(currentLocationRequest: CurrentLocationRequest): Complete<Location?> {
        return suspendCancellableCoroutine {
            fusedLocationProviderClient
                .getCurrentLocation(
                    currentLocationRequest,
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

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    suspend fun getLastLocation(): Complete<Location?> {
        return suspendCancellableCoroutine {
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

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    suspend fun getLastLocation(lastLocationRequest: LastLocationRequest): Complete<Location?> {
        return suspendCancellableCoroutine {
            fusedLocationProviderClient.getLastLocation(lastLocationRequest)
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
