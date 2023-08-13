package wing.tree.bionda.view

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
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
import wing.tree.bionda.extension.toggle
import wing.tree.bionda.model.WindowSizeClass
import wing.tree.bionda.permissions.PermissionChecker
import wing.tree.bionda.permissions.RequestMultiplePermissions
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
    override val permissions: Set<String> = buildSet {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            add(ACCESS_BACKGROUND_LOCATION)
        }

        addAll(locationPermissions)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(POST_NOTIFICATIONS)
        }
    }

    override fun onCheckSelfMultiplePermissions(result: PermissionChecker.Result) {
        if (result.granted().containsAny(locationPermissions)) {
            viewModel.load()
        }
    }

    override fun onRequestMultiplePermissionsResult(result: PermissionChecker.Result) {
        val (granted, denied) = result

        when {
            granted.containsAny(locationPermissions) -> viewModel.load()
            denied.containsAll(locationPermissions) -> viewModel.notifyPermissionsDenied(denied)
        }
    }

    override fun onShouldShowRequestMultiplePermissionsRationale(result: PermissionChecker.Result) {
        val keys = result.filter { (_, value) ->
            value.shouldShowRequestPermissionRationale
        }
            .keys
    }

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        requestMultiplePermissions()
        setContent {
            BiondaTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                val windowSizeClass = rememberWindowSizeClass()

                BackHandler(enabled = state.inSelectionMode) {
                    viewModel.inSelectionMode.toggle()
                }

                Scaffold(
                    floatingActionButton = {
                        AnimatedVisibility(
                            visible = state.inSelectionMode.not(),
                            enter = scaleIn().plus(fadeIn()),
                            exit = scaleOut().plus(fadeOut())
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    onFloatingActionButtonClick()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_add_alarm_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Column {
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
                                    onRequestPermissionsClick(it)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(windowSizeClass.marginValues)
                                    .animateContentSize()
                            )

                            Notice(
                                state = state.noticeState,
                                inSelectionMode = state.inSelectionMode,
                                onClick = {

                                },
                                onLongClick = {
                                    if (state.inSelectionMode.not()) {
                                        with(viewModel) {
                                            select(it)
                                            inSelectionMode.toggle()
                                        }
                                    }
                                },
                                onCheckedChange = { notice, checked ->
                                    viewModel.update(notice.copy(on = checked))
                                },
                                onSelectedChange = { notice, selected ->
                                    with(viewModel) {
                                        if (selected) {
                                            select(notice)
                                        } else {
                                            deselect(notice)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(Float.one)
                                    .padding(windowSizeClass.marginValues)
                            )
                        }

                        SelectionMode(
                            inSelectionMode = state.inSelectionMode,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val permissions = listOf(ACCESS_BACKGROUND_LOCATION)

            if (checkSelfSinglePermission(ACCESS_BACKGROUND_LOCATION).not()) {
                viewModel.notifyPermissionsDenied(permissions)
            } else {
                viewModel.notifyPermissionsGranted(permissions)
            }
        }
    }

    @Composable
    private fun SelectionMode(
        inSelectionMode: Boolean,
        modifier: Modifier = Modifier
    ) {
        AnimatedVisibility(
            visible = inSelectionMode,
            modifier = modifier,
            enter = slideInVertically {
                it
            },
            exit = slideOutVertically {
                it
            }
        ) {
            TabRowDefaults
            Surface(
                color = TabRowDefaults.containerColor,
                contentColor = TabRowDefaults.contentColor
            ) {
                Row {
                    Tab(
                        selected = false,
                        onClick = {
                            viewModel.alarmOn()
                        },
                        modifier = Modifier.weight(Float.one),
                        text = {
                            Text(text = stringResource(id = R.string.alarm_on))
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_alarm_on_24),
                                contentDescription = null
                            )
                        }
                    )

                    Tab(
                        selected = false,
                        onClick = { viewModel.alarmOff() },
                        modifier = Modifier.weight(Float.one),
                        text = {
                            Text(text = stringResource(id = R.string.alarm_off))
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_alarm_off_24),
                                contentDescription = null
                            )
                        }
                    )

                    Tab(
                        selected = false,
                        onClick = {
                            viewModel.deleteAll()
                        },
                        modifier = Modifier.weight(Float.one),
                        text = {
                            Text(text = stringResource(id = R.string.delete))
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }

    private fun onFloatingActionButtonClick() {
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

    private fun onRequestPermissionsClick(action: Action) {
        when (action) {
            Action.ACCESS_BACKGROUND_LOCATION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (checkSelfSinglePermission(ACCESS_BACKGROUND_LOCATION)) {
                        viewModel.notifyPermissionsGranted(listOf(ACCESS_BACKGROUND_LOCATION))
                    } else {
                        if (shouldShowRequestPermissionRationale(ACCESS_BACKGROUND_LOCATION)) {
                            requestPermissions(
                                arrayOf(ACCESS_BACKGROUND_LOCATION),
                                Int.zero
                            )
                        } else {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .apply {
                                    data = Uri.fromParts(
                                        SCHEME_PACKAGE,
                                        packageName,
                                        null
                                    )
                                }

                            startActivity(intent)
                        }
                    }
                }
            }

            Action.POST_NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (checkSelfSinglePermission(POST_NOTIFICATIONS)) {
                        viewModel.notifyPermissionsGranted(listOf(POST_NOTIFICATIONS))
                    } else {
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .apply {
                                    data = Uri.fromParts(
                                        SCHEME_PACKAGE,
                                        packageName,
                                        null
                                    )
                                }

                        startActivity(intent)
                    }
                }
            }
        }
    }
}
