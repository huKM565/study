package ru.hukm.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Автовокзал — поиск рейсов",
        state = rememberWindowState(width = 1360.dp, height = 840.dp),
    ) {
        MaterialTheme { App() }
    }
}
