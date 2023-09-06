package wing.tree.bionda.view.model

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.map
import wing.tree.bionda.extension.toCoordinate

abstract class LocationViewModel(
    application: Application
) : AndroidViewModel(application) {
    val location = MutableStateFlow<State<Location>>(State.Loading)
    val coordinate = location.map {
        it.map(Location::toCoordinate)
    }
}
