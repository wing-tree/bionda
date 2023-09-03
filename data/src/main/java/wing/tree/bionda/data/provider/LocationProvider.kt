package wing.tree.bionda.data.provider

import android.Manifest
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
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.ifFailure
import wing.tree.bionda.data.core.ifNull
import wing.tree.bionda.data.exception.OnCanceledException
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.zero
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

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun getLocation(): State.Complete<Location?> {
        val currentLocation = getCurrentLocation()
            .ifNull {
                getCurrentLocation(currentLocationRequest)
            }.ifFailure {
                Timber.e(it)
                getCurrentLocation(currentLocationRequest)
            }

        return when (currentLocation) {
            is State.Complete.Success -> currentLocation
                .ifNull {
                    getLastLocation()
                        .ifNull {
                            getLastLocation(lastLocationRequest)
                        }.ifFailure {
                            Timber.e(it)
                            getLastLocation(lastLocationRequest)
                        }
                }
            is State.Complete.Failure -> {
                Timber.e(currentLocation.exception)
                getLastLocation()
                    .ifNull {
                        getLastLocation(lastLocationRequest)
                    }.ifFailure {
                        Timber.e(it)
                        getLastLocation(lastLocationRequest)
                    }
            }
        }
    }

    @Suppress("unused")
    private suspend fun getLocationSettingsResponse(): State.Complete<LocationSettingsResponse> {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            Long.zero
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
                    it.resume(State.Complete.Failure(OnCanceledException))
                }.addOnFailureListener { exception ->
                    it.resume(State.Complete.Failure(exception))
                }.addOnSuccessListener { locationSettingsResponse ->
                    it.resume(State.Complete.Success(locationSettingsResponse))
                }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun getCurrentLocation(): State.Complete<Location?> {
        return suspendCancellableCoroutine {
            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                null
            )
                .addOnCanceledListener {
                    it.resume(State.Complete.Failure(OnCanceledException))
                }.addOnFailureListener { exception ->
                    it.resume(State.Complete.Failure(exception))
                }.addOnSuccessListener { location ->
                    it.resume(State.Complete.Success(location))
                }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun getCurrentLocation(currentLocationRequest: CurrentLocationRequest): State.Complete<Location?> {
        return suspendCancellableCoroutine {
            fusedLocationProviderClient
                .getCurrentLocation(
                    currentLocationRequest,
                    null
                ).addOnCanceledListener {
                    it.resume(State.Complete.Failure(OnCanceledException))
                }.addOnFailureListener { exception ->
                    it.resume(State.Complete.Failure(exception))
                }.addOnSuccessListener { location ->
                    it.resume(State.Complete.Success(location))
                }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun getLastLocation(): State.Complete<Location?> {
        return suspendCancellableCoroutine {
            fusedLocationProviderClient.lastLocation
                .addOnCanceledListener {
                    it.resume(State.Complete.Failure(OnCanceledException))
                }.addOnFailureListener { exception ->
                    it.resume(State.Complete.Failure(exception))
                }.addOnSuccessListener { location ->
                    it.resume(State.Complete.Success(location))
                }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun getLastLocation(lastLocationRequest: LastLocationRequest): State.Complete<Location?> {
        return suspendCancellableCoroutine {
            fusedLocationProviderClient.getLastLocation(lastLocationRequest)
                .addOnCanceledListener {
                    it.resume(State.Complete.Failure(OnCanceledException))
                }.addOnFailureListener { exception ->
                    it.resume(State.Complete.Failure(exception))
                }.addOnSuccessListener { location ->
                    it.resume(State.Complete.Success(location))
                }
        }
    }

    companion object {
        val seoul = Location(String.empty).apply {
            latitude = 37.5635694444444
            longitude = 126.980008333333
        }
    }
}
