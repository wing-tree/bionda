package wing.tree.bionda.extension

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import wing.tree.bionda.data.extension.`is`

fun AndroidViewModel.checkSelfPermission(permission: String) = getApplication<Application>()
    .checkSelfPermission(permission) `is` PackageManager.PERMISSION_GRANTED
