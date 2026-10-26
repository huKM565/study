package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hukm.COMPARISON_SIZES
import ru.hukm.ComparisonRow
import ru.hukm.Version
import ru.hukm.compareVersions

@Composable
fun VersionsScreen(onExit: () -> Unit) {
    val scope = rememberCoroutineScope()
    var rows by remember { mutableStateOf<List<ComparisonRow>>(emptyList()) }
    var busy by remember { mutableStateOf(false) }

    fun calculate() {
        busy = true
        scope.launch {
            rows = withContext(Dispatchers.Default) { compareVersions() }
            busy = false
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Title("Сравнение версий алгоритма Cycle sort")
        Caption("Время сортировки, мс")
        DataTable(
            headers = listOf("N") + Version.entries.map { "${it.title}, ${it.complexity}" },
            weights = listOf(0.4f, 1f, 1f, 1f),
            rowCount = if (rows.isEmpty()) COMPARISON_SIZES.size else rows.size,
            modifier = Modifier.fillMaxWidth().height(190.dp),
        ) { row, column ->
            val data = rows.getOrNull(row)
            when {
                column == 0 -> COMPARISON_SIZES[row].toString()
                data == null -> ""
                else -> data.millis.getValue(Version.entries[column - 1])?.let { format(it, 3) } ?: "Слишком велико"
            }
        }
        if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Button(::calculate, Modifier.weight(1f).height(52.dp), enabled = !busy) { Text("Расчёт") }
            OutlinedButton({ rows = emptyList() }, Modifier.weight(1f).height(52.dp), enabled = !busy) { Text("Очистка") }
            OutlinedButton(onExit, Modifier.weight(1f).height(52.dp)) { Text("Выход") }
        }
    }
}
