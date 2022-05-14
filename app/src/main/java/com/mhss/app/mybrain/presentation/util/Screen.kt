package com.mhss.app.mybrain.presentation.util

import com.mhss.app.mybrain.util.Constants

sealed class Screen(val route: String) {
    object Main : Screen("main_screen")
    object SpacesScreen : Screen("spaces_screen")
    object DashboardScreen : Screen("dashboard_screen")
    object SettingsScreen : Screen("settings_screen")
    object TasksScreen : Screen("tasks_screen")
    object TaskDetailScreen : Screen("task_detail_screen/{${Constants.TASK_ID_ARG}}")
    object TaskAddScreen : Screen("task_add_screen")
    object NotesScreen : Screen("notes_screen")
    object NoteDetailScreen : Screen("note_detail_screen/{${Constants.NOTE_ID_ARG}}")
    object NoteAddScreen : Screen("note_add_screen")
    object DiaryScreen : Screen("diary_screen")
    object DiaryDetailScreen : Screen("diary_detail_screen/{${Constants.DIARY_ID_ARG}}")
    object DiaryAddScreen : Screen("diary_add_screen")
    object DiarySummaryScreen : Screen("diary_summary_screen")
    object BookmarksScreen : Screen("bookmarks_screen")
    object BookmarkDetailScreen : Screen("bookmark_detail_screen/{${Constants.BOOKMARK_ID_ARG}}")
    object BookmarkAddScreen : Screen("bookmark_add_screen")
    object CalendarScreen : Screen("calendar_screen")
}