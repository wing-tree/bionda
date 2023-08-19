package wing.tree.bionda.view

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.SCHEDULE_EXACT_ALARM
import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.timepicker.MaterialTimePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.persistentListOf
import wing.tree.bionda.R
import wing.tree.bionda.data.constant.SCHEME_PACKAGE
import wing.tree.bionda.data.extension.containsAny
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.toggle
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.regular.koreaCalendar
import wing.tree.bionda.data.regular.noOperations
import wing.tree.bionda.extension.add
import wing.tree.bionda.extension.rememberWindowSizeClass
import wing.tree.bionda.extension.remove
import wing.tree.bionda.extension.requestAccessBackgroundLocationPermission
import wing.tree.bionda.extension.toggle
import wing.tree.bionda.permissions.PermissionChecker
import wing.tree.bionda.permissions.RequestMultiplePermissions
import wing.tree.bionda.permissions.locationPermissions
import wing.tree.bionda.theme.BiondaTheme
import wing.tree.bionda.view.compose.composable.Alarm
import wing.tree.bionda.view.compose.composable.Weather
import wing.tree.bionda.view.model.MainViewModel
import wing.tree.bionda.view.state.AlarmState.Action

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

    private val alarmManager by lazy {
        getSystemService(AlarmManager::class.java)
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

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        requestMultiplePermissions()
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
                            title = {
                                noOperations
                            },
                            navigationIcon = {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = null,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        )
                    },
                    floatingActionButton = {
                        AnimatedVisibility(
                            visible = inSelectionMode.not(),
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        SingleChoiceSegmentedButtonRow(
                            selectedSegmentedButtonIndex = selectedSegmentedButtonIndex,
                            onClick = {
                                selectedSegmentedButtonIndex = it
                            },
                            modifier = Modifier.padding(windowSizeClass.marginValues)
                        )

                        AnimatedContent(
                            targetState = selectedSegmentedButtonIndex,
                            modifier = Modifier.weight(Float.one),
                            transitionSpec = {
                                val slideDirection = if (targetState `is` Int.zero) {
                                    SlideDirection.Right
                                } else {
                                    SlideDirection.Left
                                }

                                slideIntoContainer(slideDirection).plus(fadeIn()) togetherWith
                                        slideOutOfContainer(slideDirection).plus(fadeOut())
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (checkSelfSinglePermission(ACCESS_BACKGROUND_LOCATION)) {
                viewModel.notifyPermissionGranted(ACCESS_BACKGROUND_LOCATION)
            } else {
                viewModel.notifyPermissionDenied(ACCESS_BACKGROUND_LOCATION)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() `is` false) {
                viewModel.notifyPermissionDenied(SCHEDULE_EXACT_ALARM)
            } else {
                viewModel.notifyPermissionGranted(SCHEDULE_EXACT_ALARM)
            }
        }
    }

    private fun onAction(action: Action, inSelectionMode: Boolean) {
        when (action) {
            is Action.Alarms -> onAlarms(action, inSelectionMode)
            is Action.RequestPermissions -> onRequestPermissions(action)
            is Action.SelectionMode -> onSelectionMode(action)
        }
    }

    private fun onAlarms(
        action: Action.Alarms,
        inSelectionMode: Boolean
    ) {
        val alarm = action.alarm

        when (action) {
            is Action.Alarms.Click -> {
                if (inSelectionMode) {
                    viewModel.selected.toggle(alarm.id)
                } else {
                    showMaterialTimePicker(
                        alarm.hour,
                        alarm.minute
                    ) { hour, minute ->
                        viewModel.update(
                            alarm.copy(
                                hour = hour,
                                minute = minute
                            )
                        )
                    }
                }
            }

            is Action.Alarms.LongClick -> {
                if (inSelectionMode.not()) {
                    viewModel.selected.toggle(alarm.id)

                    viewModel.inSelectionMode.value = true
                }
            }

            is Action.Alarms.CheckChange -> {
                viewModel.update(alarm.copy(on = action.checked))
            }

            is Action.Alarms.SelectedChange -> {
                with(viewModel.selected) {
                    if (action.selected) {
                        add(alarm.id)
                    } else {
                        remove(alarm.id)
                    }
                }
            }

            is Action.Alarms.ConditionClick -> {
                viewModel.update(
                    with(alarm) {
                        copy(
                            conditions = conditions.toggle(
                                action.condition
                            )
                        )
                    }
                )
            }
        }
    }

    private fun onFloatingActionButtonClick() {
        val koreaCalendar = koreaCalendar()

        showMaterialTimePicker(
            hour = koreaCalendar.hourOfDay,
            minute = koreaCalendar.minute
        ) { hour, minute ->
            viewModel.add(hour, minute)
        }
    }

    private fun onRequestPermissions(action: Action.RequestPermissions) {
        when (action) {
            Action.RequestPermissions.ACCESS_BACKGROUND_LOCATION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (checkSelfSinglePermission(ACCESS_BACKGROUND_LOCATION)) {
                        viewModel.notifyPermissionGranted(ACCESS_BACKGROUND_LOCATION)
                    } else {
                        requestAccessBackgroundLocationPermission()
                    }
                }
            }

            Action.RequestPermissions.POST_NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (checkSelfSinglePermission(POST_NOTIFICATIONS)) {
                        viewModel.notifyPermissionGranted(POST_NOTIFICATIONS)
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

            Action.RequestPermissions.SCHEDULE_EXACT_ALARM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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

    private fun showMaterialTimePicker(
        hour: Int,
        minute: Int,
        onPositiveButtonClick: (hour: Int, minute: Int) -> Unit
    ) {
        val materialTimePicker = MaterialTimePicker.Builder()
            .setHour(hour)
            .setMinute(minute)
            .build()

        materialTimePicker.also {
            it.addOnPositiveButtonClickListener { _ ->
                onPositiveButtonClick(
                    it.hour,
                    it.minute
                )
            }

            it.show(supportFragmentManager, it.tag)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleChoiceSegmentedButtonRow(
    selectedSegmentedButtonIndex: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = persistentListOf(
        R.string.weather,
        R.string.alarm
    )

    SingleChoiceSegmentedButtonRow(
        modifier = modifier.semantics(false) {
            SemanticsProperties.SelectableGroup
        }
    ) {
        val colors = SegmentedButtonDefaults.colors(
            activeContainerColor = Color.Transparent,
            activeContentColor = colorScheme.primary,
            activeBorderColor = Color.Transparent,
            inactiveContainerColor = Color.Transparent,
            inactiveBorderColor = Color.Transparent
        )

        val rippleTheme = remember {
            object : RippleTheme {
                @Composable
                override fun defaultColor(): Color = Color.Red

                @Composable
                override fun rippleAlpha(): RippleAlpha = RippleAlpha(
                    draggedAlpha = Float.zero,
                    focusedAlpha = Float.zero,
                    hoveredAlpha = Float.zero,
                    pressedAlpha = Float.zero
                )

            }
        }

        CompositionLocalProvider(
            LocalRippleTheme provides rippleTheme
        ) {
            items.forEachIndexed { index, item ->
                SegmentedButton(
                    selected = index `is` selectedSegmentedButtonIndex,
                    onClick = {
                        onClick(index)
                    },
                    colors = colors
                ) {
                    Text(stringResource(id = item))
                }
            }
        }
    }
}
