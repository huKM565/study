package ru.hukm.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "BP Sea Company — кратчайшие пути (алгоритм Флойда)",
        state = rememberWindowState(width = 1480.dp, height = 900.dp),
    ) {
        MaterialTheme { App() }
    }
}
