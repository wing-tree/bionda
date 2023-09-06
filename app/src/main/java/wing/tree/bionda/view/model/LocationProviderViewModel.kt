package wing.tree.bionda.view.model

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.map
import wing.tree.bionda.data.extension.ifTrue
import wing.tree.bionda.data.provider.LocationProvider
import wing.tree.bionda.extension.checkSelfPermission
import wing.tree.bionda.extension.toCoordinate
import wing.tree.bionda.permissions.locationPermissions

abstract class LocationProviderViewModel(
    application: Application,
    private val locationProvider: LocationProvider
) : AndroidViewModel(application) {
    val location = MutableStateFlow<State<Location>>(State.Loading)
    val coordinate = location.map {
        it.map(Location::toCoordinate)
    }

    fun load() {
        locationPermissions.any {
            checkSelfPermission(it)
        }.ifTrue {
            viewModelScope.launch {
                location.value = locationProvider.getLocation().map {
                    it ?: LocationProvider.seoul
                }
            }
        }
    }
}
