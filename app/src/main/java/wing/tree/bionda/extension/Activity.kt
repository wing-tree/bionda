package wing.tree.bionda.extension

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.window.layout.WindowMetricsCalculator
import wing.tree.bionda.R
import wing.tree.bionda.data.constant.SCHEME_PACKAGE
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.model.WindowSizeClass

fun Activity.Intent(cls: Class<*>) = Intent(this, cls)

fun Activity.launchApplicationDetailsSettings() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts(SCHEME_PACKAGE, packageName, null)
    }.let {
        startActivity(it)
    }
}

@Composable
fun Activity.rememberWindowSizeClass(): WindowSizeClass {
    val windowSize = rememberWindowSize()

    val windowDpSize = with(LocalDensity.current) {
        windowSize.toDpSize()
    }

    return WindowSizeClass.get(windowDpSize)
}

@Composable
private fun Activity.rememberWindowSize(): Size {
    val configuration = LocalConfiguration.current
    val currentWindowMetrics = remember(configuration) {
        WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
    }

    return currentWindowMetrics.bounds.toComposeRect().size
}

@RequiresApi(Build.VERSION_CODES.Q)
fun Activity.requestAccessBackgroundLocationPermission() {
    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            Int.zero
        )
    } else {
        launchApplicationDetailsSettings()
    }
}

fun Activity.shareApp() {
    val intent = Intent(Intent.ACTION_SEND)
    val text = "https://play.google.com/store/apps/details?id=${packageName}"

    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, text)

    Intent.createChooser(intent, getString(R.string.share_the_app)).also {
        it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(it)
    }
}

inline fun <reified T : Activity> Activity.startActivity() {
    startActivity(Intent(this, T::class.java))
}
