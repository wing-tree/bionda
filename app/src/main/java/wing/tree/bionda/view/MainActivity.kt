package wing.tree.bionda.view

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.timepicker.MaterialTimePicker
import dagger.hilt.android.AndroidEntryPoint
import wing.tree.bionda.data.extension.ONE
import wing.tree.bionda.data.extension.containsAny
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.permissions.RequestMultiplePermissions
import wing.tree.bionda.permissions.Result
import wing.tree.bionda.theme.BiondaTheme
import wing.tree.bionda.view.compose.composable.Forecast
import wing.tree.bionda.view.compose.composable.Notice
import wing.tree.bionda.view.compose.composable.RequestPermissions
import wing.tree.bionda.view.model.MainViewModel
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), RequestMultiplePermissions {
    override val launcher = registerForActivityResult()
    override val permissions: Array<String> = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(ACCESS_BACKGROUND_LOCATION)
        }

        add(ACCESS_COARSE_LOCATION)
        add(ACCESS_FINE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(POST_NOTIFICATIONS)
        }
    }
        .toTypedArray()

    override fun onCheckSelfMultiplePermissions(result: Result) {
        val granted = result.granted()

        if (granted.containsAny(listOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION))) {
            viewModel.load()
        }
    }

    override fun onRequestMultiplePermissionsResult(result: Result) {
        val granted = result.granted()

        if (granted.containsAny(listOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION))) {
            viewModel.load()
        } else {
            viewModel.notifyMultiplePermissionsDenied(result.denied())
        }
    }

    override fun onShouldShowRequestMultiplePermissionsRationale(result: Result) {
        val keys = result.filter { (_, value) ->
            value.shouldShowRequestPermissionRationale
        }
            .keys
    }

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestMultiplePermissions()

        setContent {
            BiondaTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()

                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                val calendar = Calendar.getInstance(Locale.KOREA)

                                val materialTimePicker = MaterialTimePicker.Builder()
                                    .setHour(calendar.hourOfDay)
                                    .setMinute(calendar.minute)
                                    .build()

                                with(materialTimePicker) {
                                    addOnPositiveButtonClickListener {
                                        viewModel.add(hour, minute)
                                    }

                                    show(supportFragmentManager, tag)
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Forecast(
                            state = state.forecastState,
                            modifier = Modifier.fillMaxWidth()
                        )

                        RequestPermissions(
                            state = state.requestPermissionsState,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Notice(
                            state = state.noticeState,
                            onClick = {

                            },
                            onLongClick = {
                                viewModel.delete(it)
                            },
                            onCheckedChange = { notice, checked ->
                                viewModel.update(notice.copy(checked = checked))
                            },
                            modifier = Modifier.weight(Float.ONE)
                        )
                    }
                }
            }
        }
    }
}
