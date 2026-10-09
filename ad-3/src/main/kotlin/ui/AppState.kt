package ru.hukm.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.hukm.Method
import ru.hukm.Result
import ru.hukm.methods

class AppState {
    var source by mutableStateOf<DoubleArray?>(null)
    var sorted by mutableStateOf<DoubleArray?>(null)
    var selectedMethod by mutableStateOf<Method>(methods[0])

    var lastResult by mutableStateOf<Result?>(null)
    var analysis by mutableStateOf<List<Result>>(emptyList())

    var busy by mutableStateOf(false)
    var status by mutableStateOf<String?>(null)

    fun clear() {
        source = null
        sorted = null
        lastResult = null
        analysis = emptyList()
        status = null
    }
}
