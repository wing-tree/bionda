package wing.tree.bionda.view

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.timepicker.MaterialTimePicker
import dagger.hilt.android.AndroidEntryPoint
import wing.tree.bionda.R
import wing.tree.bionda.data.constant.SCHEME_PACKAGE
import wing.tree.bionda.data.extension.containsAny
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.extension.rememberWindowSizeClass
import wing.tree.bionda.model.WindowSizeClass
import wing.tree.bionda.permissions.RequestMultiplePermissions
import wing.tree.bionda.permissions.Result
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.theme.BiondaTheme
import wing.tree.bionda.view.compose.composable.Forecast
import wing.tree.bionda.view.compose.composable.Notice
import wing.tree.bionda.view.compose.composable.RequestPermissions
import wing.tree.bionda.view.compose.composable.VerticalSpacer
import wing.tree.bionda.view.model.MainViewModel
import wing.tree.bionda.view.state.MainState.Action
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), RequestMultiplePermissions {
    override val launcher = registerForActivityResult()
    override val permissions: Array<String> = buildList {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            add(ACCESS_BACKGROUND_LOCATION)
        }

        addAll(locationPermissions)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(POST_NOTIFICATIONS)
        }
    }
        .toTypedArray()

    override fun onCheckSelfMultiplePermissions(result: Result) {
        val granted = result.granted()

        if (granted.containsAny(locationPermissions)) {
            viewModel.load()
        }
    }

    override fun onRequestMultiplePermissionsResult(result: Result) {
        with(result) {
            val granted = granted()
            val denied = denied()

            when {
                granted.containsAny(locationPermissions) -> viewModel.load()
                denied.containsAll(locationPermissions) -> viewModel.notifyPermissionsDenied(denied)
            }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (shouldShowRequestPermissionRationale(ACCESS_BACKGROUND_LOCATION)) {
                viewModel.notifyPermissionsDenied(listOf(ACCESS_BACKGROUND_LOCATION))
            }
        }

        setContent {
            BiondaTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                val windowSizeClass = rememberWindowSizeClass()

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
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_add_alarm_24),
                                contentDescription = null
                            )
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
                            windowSizeClass = windowSizeClass,
                            modifier = Modifier.fillMaxWidth()
                        )

                        VerticalSpacer(
                            height = when (windowSizeClass) {
                                is WindowSizeClass.Compact -> 16.dp
                                else -> 24.dp
                            }
                        )

                        RequestPermissions(
                            state = state.requestPermissionsState,
                            onClick = {
                                when (it) {
                                    Action.ACCESS_BACKGROUND_LOCATION -> {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                            if (checkSelfPermission(*arrayOf(ACCESS_BACKGROUND_LOCATION)).not()) {
                                                if (shouldShowRequestPermissionRationale(ACCESS_BACKGROUND_LOCATION)) {
                                                    requestPermissions(
                                                        arrayOf(ACCESS_BACKGROUND_LOCATION),
                                                        Int.zero
                                                    )
                                                } else {
                                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                        data = Uri.fromParts(
                                                            SCHEME_PACKAGE,
                                                            packageName,
                                                            null
                                                        )
                                                    }

                                                    startActivity(intent)
                                                }
                                            } else {
                                                viewModel.notifyPermissionsGranted(listOf(ACCESS_BACKGROUND_LOCATION))
                                            }
                                        }
                                    }

                                    Action.POST_NOTIFICATIONS -> {}
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(windowSizeClass.marginValues)
                                .animateContentSize()
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
                            modifier = Modifier
                                .weight(Float.one)
                                .padding(windowSizeClass.marginValues)
                        )
                    }
                }
            }
        }
    }
}
