package wing.tree.bionda.view.model

import android.app.Application
import android.location.Location
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.core.map
import wing.tree.bionda.data.extension.ifTrue
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.data.provider.LocationProvider
import wing.tree.bionda.extension.checkSelfPermission
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.permissions.locationPermissions

abstract class LocationProviderViewModel(
    application: Application,
    private val locationProvider: LocationProvider
) : BaseViewModel(application) {
    val location = MutableStateFlow<State<Location>>(State.Loading)
    val coordinate = location.map {
        it.map(Location::toCoordinate)
    }

    var area: Area? = null
        set(value) {
            if (value.isNotNull()) {
                field = value

                location.value = Location(null)
                    .apply {
                        latitude = value.latitude
                        longitude = value.longitude
                    }.let {
                        Complete.Success(it)
                    }
            }
        }

    fun load() {
        viewModelScope.launch {
            area?.let {
                location.value = Location(null)
                    .apply {
                        latitude = it.latitude
                        longitude = it.longitude
                    }.let {
                        Complete.Success(it)
                    }
            } ?: locationPermissions.any {
                checkSelfPermission(it)
            }.ifTrue {
                location.value = locationProvider.getLocation().map {
                    it ?: LocationProvider.seoul
                }
            }
        }
    }
}
