package wing.tree.bionda.view.model

import android.app.Application
import android.location.Location
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.core.map
import wing.tree.bionda.data.extension.flatMap
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.data.provider.LocationProvider
import wing.tree.bionda.data.source.local.AreaDataSource
import wing.tree.bionda.model.Coordinate
import javax.inject.Inject

abstract class LocationProviderViewModel(application: Application) : BaseViewModel(application) {
    @Inject
    lateinit var areaDataSource: AreaDataSource

    @Inject
    lateinit var locationProvider: LocationProvider

    private val _area = MutableStateFlow<Area?>(null)
    private val _location = MutableStateFlow<State<Location>>(State.Loading)

    val area = combine(_area, _location) { area, location ->
        when {
            area.isNotNull() -> Complete.Success(area)
            else -> location.map {
                areaDataSource.nearestArea(it)
            }
        }
    }

    val location = combine(_area, _location) { area, location ->
        if (area.isNotNull()) {
            Location(null)
                .apply {
                    latitude = area.latitude
                    longitude = area.longitude
                }.let {
                    Complete.Success(it)
                }
        } else {
            location
        }
    }
        .stateIn(initialValue = State.Loading)

    val coordinate = area.flatMap {
        Complete.Success(Coordinate(nx = it.nx, ny = it.ny))
    }
        .stateIn(State.Loading)

    fun load() {
        viewModelScope.launch {
            locationProvider.location
                .collect {
                    _location.value = it.map { location ->
                        location ?: LocationProvider.seoul
                    }
                }
        }
    }

    fun updateArea(value: Area?) {
        _area.update { _ ->
            value
        }
    }

    fun updateLocation(value: State<Location>) {
        _location.update { _ ->
            value
        }
    }
}
