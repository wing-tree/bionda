package wing.tree.bionda.view

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.SCHEDULE_EXACT_ALARM
import android.app.AlarmManager
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import wing.tree.bionda.data.extension.containsAny
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.full
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.toggle
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.extension.add
import wing.tree.bionda.extension.launchApplicationDetailsSettings
import wing.tree.bionda.extension.rememberWindowSizeClass
import wing.tree.bionda.extension.remove
import wing.tree.bionda.extension.requestAccessBackgroundLocationPermission
import wing.tree.bionda.extension.showMaterialTimePicker
import wing.tree.bionda.extension.toggle
import wing.tree.bionda.permissions.PermissionChecker
import wing.tree.bionda.permissions.RequestMultiplePermissions
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.theme.BiondaTheme
import wing.tree.bionda.top.level.noOperations
import wing.tree.bionda.view.compose.composable.SingleChoiceSegmentedButtonRow
import wing.tree.bionda.view.compose.composable.alarm.Alarm
import wing.tree.bionda.view.compose.composable.weather.Weather
import wing.tree.bionda.view.model.MainViewModel
import wing.tree.bionda.view.state.AlarmState.Action

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PermissionChecker {
    private val alarmManager by lazy {
        getSystemService(AlarmManager::class.java)
    }

    private val permissions: Set<String> = buildSet {
        if (VERSION.SDK_INT == VERSION_CODES.Q) {
            add(ACCESS_BACKGROUND_LOCATION)
        }

        addAll(locationPermissions)

        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            add(POST_NOTIFICATIONS)
        }
    }

    private val requestMultiplePermissions = RequestMultiplePermissions().also {
        it.initialize(this)
    }

    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val (granted, denied) = requestMultiplePermissions.request(permissions)

                with(viewModel) {
                    if (granted.containsAny(locationPermissions)) {
                        load()
                    }

                    notifyPermissionsDenied(denied)
                }
            }
        }

        setContent {
            BiondaTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                val inSelectionMode = state.inSelectionMode
                val windowSizeClass = rememberWindowSizeClass()

                var selectedSegmentedButtonIndex by remember {
                    mutableIntStateOf(Int.zero)
                }

                BackHandler(enabled = inSelectionMode) {
                    viewModel.inSelectionMode.toggle()
                }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = noOperations,
                            navigationIcon = {
                                SingleChoiceSegmentedButtonRow(
                                    selectedSegmentedButtonIndex = selectedSegmentedButtonIndex,
                                    onClick = {
                                        selectedSegmentedButtonIndex = it
                                    },
                                    modifier = Modifier.padding(windowSizeClass.marginValues)
                                )
                            },
                            actions = {
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = null
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = colorScheme.background
                            )
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = selectedSegmentedButtonIndex,
                            modifier = Modifier.weight(Float.full),
                            transitionSpec = {
                                val slideDirection = if (targetState `is` Int.zero) {
                                    SlideDirection.Right
                                } else {
                                    SlideDirection.Left
                                }

                                val enter = slideIntoContainer(slideDirection).plus(fadeIn())
                                val exit = slideOutOfContainer(slideDirection).plus(fadeOut())

                                enter togetherWith exit
                            },
                            label = String.empty
                        ) { targetState ->
                            when (targetState) {
                                Int.zero -> Weather(
                                    state = state.weatherState,
                                    windowSizeClass = windowSizeClass,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Int.one -> Alarm(
                                    state = state.alarmState,
                                    inSelectionMode = inSelectionMode,
                                    onAction = {
                                        onAction(it, inSelectionMode)
                                    },
                                    windowSizeClass = windowSizeClass
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            if (checkSelfSinglePermission(ACCESS_BACKGROUND_LOCATION)) {
                viewModel.notifyPermissionGranted(ACCESS_BACKGROUND_LOCATION)
            } else {
                viewModel.notifyPermissionDenied(ACCESS_BACKGROUND_LOCATION)
            }
        }

        if (VERSION.SDK_INT >= VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() `is` false) {
                viewModel.notifyPermissionDenied(SCHEDULE_EXACT_ALARM)
            } else {
                viewModel.notifyPermissionGranted(SCHEDULE_EXACT_ALARM)
            }
        }

        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            if (checkSelfSinglePermission(POST_NOTIFICATIONS)) {
                viewModel.notifyPermissionGranted(POST_NOTIFICATIONS)
            } else {
                viewModel.notifyPermissionDenied(POST_NOTIFICATIONS)
            }
        }
    }

    private fun onAction(action: Action, inSelectionMode: Boolean) {
        when (action) {
            Action.Add -> onAddAlarmClick()
            is Action.Alarms -> onAlarms(action, inSelectionMode)
            is Action.RequestPermissions -> onRequestPermissions(action)
            is Action.SelectionMode -> onSelectionMode(action)
        }
    }

    private fun onAddAlarmClick() = showMaterialTimePicker(koreaCalendar) { hour, minute ->
        viewModel.add(hour, minute)
    }

    private fun onAlarms(
        action: Action.Alarms,
        inSelectionMode: Boolean
    ) {
        val alarm = action.alarm

        when (action) {
            is Action.Alarms.Click -> if (inSelectionMode) {
                viewModel.selected.toggle(alarm.id)
            } else {
                showMaterialTimePicker(hour = alarm.hour, minute = alarm.minute) { hour, minute ->
                    viewModel.update(alarm.copy(hour = hour, minute = minute))
                }
            }

            is Action.Alarms.LongClick -> if (inSelectionMode.not()) {
                viewModel.selected.toggle(alarm.id)

                viewModel.inSelectionMode.value = true
            }

            is Action.Alarms.CheckChange -> viewModel.update(alarm.copy(on = action.checked))
            is Action.Alarms.SelectedChange -> with(viewModel.selected) {
                if (action.selected) {
                    add(alarm.id)
                } else {
                    remove(alarm.id)
                }
            }

            is Action.Alarms.ConditionClick -> viewModel.update(
                with(alarm) {
                    copy(conditions = conditions.toggle(action.condition))
                }
            )
        }
    }

    private fun onRequestPermissions(action: Action.RequestPermissions) {
        when (action) {
            Action.RequestPermissions.ACCESS_BACKGROUND_LOCATION -> if (VERSION.SDK_INT >= VERSION_CODES.Q) {
                if (checkSelfSinglePermission(ACCESS_BACKGROUND_LOCATION)) {
                    viewModel.notifyPermissionGranted(ACCESS_BACKGROUND_LOCATION)
                } else {
                    requestAccessBackgroundLocationPermission()
                }
            }

            Action.RequestPermissions.POST_NOTIFICATIONS -> if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                if (checkSelfSinglePermission(POST_NOTIFICATIONS)) {
                    viewModel.notifyPermissionGranted(POST_NOTIFICATIONS)
                } else {
                    launchApplicationDetailsSettings()
                }
            }

            Action.RequestPermissions.SCHEDULE_EXACT_ALARM -> {
                if (VERSION.SDK_INT >= VERSION_CODES.S) {
                    if (alarmManager?.canScheduleExactAlarms() `is` false) {
                        startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                    } else {
                        viewModel.notifyPermissionGranted(SCHEDULE_EXACT_ALARM)
                    }
                }
            }
        }
    }

    private fun onSelectionMode(action: Action.SelectionMode) {
        when (action) {
            Action.SelectionMode.ALARM_OFF -> viewModel.alarmOff()
            Action.SelectionMode.ALARM_ON -> viewModel.alarmOn()
            Action.SelectionMode.DELETE_ALL -> viewModel.deleteAll()
        }
    }
}
