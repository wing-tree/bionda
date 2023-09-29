package wing.tree.bionda.view.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import wing.tree.bionda.data.extension.fiveSecondsInMilliseconds

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected fun <T> Flow<T>.stateIn(initialValue: T) = stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Long.fiveSecondsInMilliseconds),
        initialValue = initialValue
    )
}
