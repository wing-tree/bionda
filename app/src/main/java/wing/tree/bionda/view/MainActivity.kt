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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.hourOfDay
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.minute
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.toggle
import wing.tree.bionda.data.extension.two
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
import wing.tree.bionda.view.compose.composable.Forecast
import wing.tree.bionda.view.compose.composable.Notice
import wing.tree.bionda.view.compose.composable.RequestPermissions
import wing.tree.bionda.view.model.MainViewModel
import wing.tree.bionda.view.state.MainState.Action
import wing.tree.bionda.view.state.NoticeState

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

                var selectedTabIndex by remember {
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
                            modifier = Modifier.padding(windowSizeClass.marginValues)
                        ) {
                            SegmentedButton(
                                selected = Int.zero `is` selectedTabIndex,
                                onClick = {
                                    selectedTabIndex = Int.zero
                                },
                                shape = SegmentedButtonDefaults.shape(
                                    position = Int.zero,
                                    count = Int.two
                                )
                            ) {
                                Text(stringResource(id = R.string.weather))
                            }

                            SegmentedButton(
                                selected = Int.one `is` selectedTabIndex,
                                onClick = {
                                    selectedTabIndex = Int.one
                                },
                                shape = SegmentedButtonDefaults.shape(
                                    position = Int.one,
                                    count = Int.two
                                )
                            ) {
                                Text(stringResource(id = R.string.alarm))
                            }
                        }

                        AnimatedContent(
                            targetState = selectedTabIndex,
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
                                Int.zero -> Forecast(
                                    state = state.forecastState,
                                    windowSizeClass = windowSizeClass,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Int.one -> Box(modifier = Modifier.fillMaxSize()) {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        RequestPermissions(
                                            state = state.requestPermissionsState,
                                            onClick = {
                                                onRequestPermissionsClick(it)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .animateContentSize()
                                        )

                                        Notice(
                                            state = state.noticeState,
                                            inSelectionMode = inSelectionMode,
                                            onAction = {
                                                val notice = it.notice

                                                when (it) {
                                                    is NoticeState.Action.Click -> {
                                                        if (inSelectionMode) {
                                                            viewModel.selected.toggle(notice.id)
                                                        } else {
                                                            showMaterialTimePicker(
                                                                notice.hour,
                                                                notice.minute
                                                            ) { hour, minute ->
                                                                viewModel.update(
                                                                    notice.copy(
                                                                        hour = hour,
                                                                        minute = minute
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    is NoticeState.Action.LongClick -> {
                                                        if (inSelectionMode.not()) {
                                                            viewModel.selected.toggle(notice.id)

                                                            viewModel.inSelectionMode.value = true
                                                        }
                                                    }

                                                    is NoticeState.Action.CheckChange -> {
                                                        viewModel.update(notice.copy(on = it.checked))
                                                    }

                                                    is NoticeState.Action.SelectedChange -> {
                                                        with(viewModel.selected) {
                                                            if (it.selected) {
                                                                add(notice.id)
                                                            } else {
                                                                remove(notice.id)
                                                            }
                                                        }
                                                    }

                                                    is NoticeState.Action.ConditionClick -> {
                                                        viewModel.update(
                                                            with(notice) {
                                                                copy(
                                                                    conditions = conditions.toggle(
                                                                        it.condition
                                                                    )
                                                                )
                                                            }
                                                        )
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
                        onClick = {
                            viewModel.alarmOff()
                        },
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
        val koreaCalendar = koreaCalendar()

        showMaterialTimePicker(
            hour = koreaCalendar.hourOfDay,
            minute = koreaCalendar.minute
        ) { hour, minute ->
            viewModel.add(hour, minute)
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

    private fun onRequestPermissionsClick(action: Action) {
        when (action) {
            Action.ACCESS_BACKGROUND_LOCATION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (checkSelfSinglePermission(ACCESS_BACKGROUND_LOCATION)) {
                        viewModel.notifyPermissionGranted(ACCESS_BACKGROUND_LOCATION)
                    } else {
                        requestAccessBackgroundLocationPermission()
                    }
                }
            }

            Action.POST_NOTIFICATIONS -> {
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

            Action.SCHEDULE_EXACT_ALARM -> {
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
}
