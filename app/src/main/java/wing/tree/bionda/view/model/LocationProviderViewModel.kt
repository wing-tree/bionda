package wing.tree.bionda.view.model

import android.app.Application
import android.location.Location
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.core.map
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.data.provider.LocationProvider
import wing.tree.bionda.extension.toCoordinate

abstract class LocationProviderViewModel(
    application: Application,
    private val locationProvider: LocationProvider
) : BaseViewModel(application) {
    private val area = MutableStateFlow<Area?>(null)

    private val _location = MutableStateFlow<State<Location?>>(State.Loading)
    val location = combine(locationProvider.location, area) { location, area ->
        if (area.isNotNull()) {
            Location(null)
                .apply {
                    latitude = area.latitude
                    longitude = area.longitude
                }.let {
                    Complete.Success(it)
                }
        } else {
            location.map {
                it ?: LocationProvider.seoul
            }
        }
    }
        .stateIn(initialValue = State.Loading)

    val coordinate = location.map {
        it.map(Location::toCoordinate)
    }

    fun load() {
        viewModelScope.launch {
            locationProvider.location
                .collect {
                    _location.value = it
                }
        }
    }

    fun updateArea(value: Area?) {
        area.update { _ ->
            value
        }
    }

    fun updateLocation(value: State<Location>) {
        _location.update { _ ->
            value
        }
    }
}
