package ru.hukm.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main(): Unit = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ЛР №3. Сортировка данных (вариант 9, Logistic)",
        state = rememberWindowState(size = DpSize(1280.dp, 1070.dp)),
    ) {
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                App()
            }
        }
    }
}
