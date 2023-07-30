package wing.tree.bionda.extension

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel

fun AndroidViewModel.checkSelfPermission(vararg permission: String) = permission.all {
    getApplication<Application>().checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
}
