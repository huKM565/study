package ru.hukm.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    var taskOpen by remember { mutableStateOf(false) }
    var appOpen by remember { mutableStateOf(false) }
    var sortOpen by remember { mutableStateOf(false) }
    var versionsOpen by remember { mutableStateOf(false) }
    var regressionOpen by remember { mutableStateOf(false) }
    var theoryOpen by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Курсовая работа АиСД",
        state = rememberWindowState(width = 1100.dp, height = 720.dp),
    ) {
        Screen {
            TitleScreen(onTask = { taskOpen = true }, onApp = { appOpen = true }, onExit = ::exitApplication)
        }
    }
    if (taskOpen) {
        SubWindow("Техническое задание", 1100, 800, { taskOpen = false }) {
            TaskScreen(onExit = { taskOpen = false })
        }
    }
    if (appOpen) {
        SubWindow("Сортировка данных", 1100, 640, { appOpen = false }) {
            AppScreen(
                onSort = { sortOpen = true },
                onVersions = { versionsOpen = true },
                onRegression = { regressionOpen = true },
                onExit = { appOpen = false },
            )
        }
    }
    if (sortOpen) {
        SubWindow("Метод Cycle sort", 1300, 800, { sortOpen = false }) {
            SortScreen(onExit = { sortOpen = false })
        }
    }
    if (versionsOpen) {
        SubWindow("Сравнение версий", 1300, 460, { versionsOpen = false }) {
            VersionsScreen(onExit = { versionsOpen = false })
        }
    }
    if (regressionOpen) {
        SubWindow("Уравнение связи", 1440, 900, { regressionOpen = false }) {
            RegressionScreen(onTheory = { theoryOpen = true }, onExit = { regressionOpen = false })
        }
    }
    if (theoryOpen) {
        SubWindow("Теоретическое обоснование", 1300, 820, { theoryOpen = false }) {
            TheoryScreen(onExit = { theoryOpen = false })
        }
    }
}
