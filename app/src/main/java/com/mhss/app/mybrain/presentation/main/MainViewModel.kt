package com.mhss.app.mybrain.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhss.app.mybrain.domain.model.calendar.CalendarEvent
import com.mhss.app.mybrain.domain.model.diary.DiaryEntry
import com.mhss.app.mybrain.domain.model.tasks.Task
import com.mhss.app.mybrain.domain.use_case.calendar.GetAllEventsUseCase
import com.mhss.app.mybrain.domain.use_case.diary.GetAllEntriesUseCase
import com.mhss.app.mybrain.domain.use_case.settings.GetPreferenceUseCase
import com.mhss.app.mybrain.domain.use_case.settings.SavePreferenceUseCase
import com.mhss.app.mybrain.domain.use_case.tasks.GetAllTasksUseCase
import com.mhss.app.mybrain.domain.use_case.tasks.UpdateTaskUseCase
import com.mhss.app.mybrain.ui.theme.Rubik
import com.mhss.app.mybrain.util.Constants
import com.mhss.app.mybrain.util.date.formatDateForMapping
import com.mhss.app.mybrain.util.date.inTheLastWeek
import com.mhss.app.mybrain.util.settings.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(
    private val getPreference: GetPreferenceUseCase,
    private val savePreference: SavePreferenceUseCase,
    private val getAllTasks: GetAllTasksUseCase,
    private val getAllEntriesUseCase: GetAllEntriesUseCase,
    private val updateTask: UpdateTaskUseCase,
    private val getAllEventsUseCase: GetAllEventsUseCase
) : ViewModel() {

    var uiState by mutableStateOf(UiState())
    private set

    private var refreshTasksJob : Job? = null

    val lockApp = getPreference(booleanPreferencesKey(Constants.LOCK_APP_KEY), false)
    val themeMode = getPreference(intPreferencesKey(Constants.SETTINGS_THEME_KEY), ThemeSettings.AUTO.value)
    val defaultStartUpScreen = getPreference(intPreferencesKey(Constants.DEFAULT_START_UP_SCREEN_KEY), StartUpScreenSettings.SPACES.value)
    val font = getPreference(intPreferencesKey(Constants.APP_FONT_KEY), Rubik.toInt())
    val blockScreenshots = getPreference(booleanPreferencesKey(Constants.BLOCK_SCREENSHOTS_KEY), false)

    fun onDashboardEvent(event: DashboardEvent) {
        when(event) {
            is DashboardEvent.ReadPermissionChanged -> {
                if (event.hasPermission)
                    getCalendarEvents()
            }
            is DashboardEvent.UpdateTask -> viewModelScope.launch {
                updateTask(event.task, event.task)
            }
            DashboardEvent.InitAll -> collectDashboardData()
        }
    }

    data class UiState(
        val dashBoardTasks: List<Task> = emptyList(),
        val dashBoardEvents: Map<String, List<CalendarEvent>> = emptyMap(),
        val summaryTasks: List<Task> = emptyList(),
        val dashBoardEntries: List<DiaryEntry> = emptyList()
    )

    private fun getCalendarEvents() = viewModelScope.launch {
        val excluded = getPreference(
            stringSetPreferencesKey(Constants.EXCLUDED_CALENDARS_KEY),
            emptySet()
        ).first()
        val events = getAllEventsUseCase(excluded.toIntList()) {
            it.start.formatDateForMapping()
        }
        uiState = uiState.copy(
            dashBoardEvents = events
        )
    }

    private fun collectDashboardData() = viewModelScope.launch {
        combine(
            getPreference(
                intPreferencesKey(Constants.TASKS_ORDER_KEY),
                Order.DateModified(OrderType.ASC()).toInt()
            ),
            getPreference(
                booleanPreferencesKey(Constants.SHOW_COMPLETED_TASKS_KEY),
                false
            ),
            getAllEntriesUseCase(Order.DateCreated(OrderType.ASC()))
        ) { order, showCompleted, entries ->
            uiState = uiState.copy(
                dashBoardEntries = entries,
            )
            refreshTasks(order.toOrder(), showCompleted)
        }.collect()
    }

    private fun refreshTasks(order: Order, showCompleted: Boolean) {
        refreshTasksJob?.cancel()
        refreshTasksJob = getAllTasks(order).onEach { tasks ->
                uiState = uiState.copy(
                    dashBoardTasks = if (showCompleted) tasks else tasks.filter { !it.isCompleted },
                    summaryTasks = tasks.filter { it.createdDate.inTheLastWeek() }
                )
            }.launchIn(viewModelScope)
    }

    fun disableAppLock() = viewModelScope.launch {
        savePreference(booleanPreferencesKey(Constants.LOCK_APP_KEY), false)
    }

}