package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hukm.analyze
import ru.hukm.generateArray
import ru.hukm.measure
import ru.hukm.round2
import kotlin.system.exitProcess

@Composable
fun BottomPanel(state: AppState) {
    val scope = rememberCoroutineScope()

    Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), elevation = 2.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CounterField(
                    label = "Время сортировки, мс",
                    value = state.lastResult?.let { round2(it.timeMs).toString() }.orEmpty(),
                    modifier = Modifier.weight(1f),
                )
                CounterField(
                    label = "Количество сравнений",
                    value = state.lastResult?.counter?.comparisons?.toString().orEmpty(),
                    modifier = Modifier.weight(1f),
                )
                CounterField(
                    label = "Количество перестановок",
                    value = state.lastResult?.counter?.moves?.toString().orEmpty(),
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    enabled = !state.busy,
                    onClick = {
                        val method = state.selectedMethod
                        state.busy = true
                        state.status = "Сортировка методом «${method.name}»…"
                        scope.launch(Dispatchers.Default) {
                            val source = state.source ?: generateArray()
                            val result = measure(method, source)
                            withContext(Dispatchers.Main) {
                                state.source = source
                                state.sorted = source.sortedArray()
                                state.lastResult = result
                                state.busy = false
                                state.status = "Готово: ${method.name}, ${round2(result.timeMs)} мс"
                            }
                        }
                    },
                ) { Text("Сортировка") }

                Button(
                    enabled = !state.busy,
                    onClick = {
                        state.busy = true
                        val shown = state.selectedMethod
                        state.status = "Анализ всех методов…"
                        scope.launch(Dispatchers.Default) {
                            val source = state.source ?: generateArray()
                            val results = analyze(source)
                            val shownResult = results.first { it.method == shown }
                            withContext(Dispatchers.Main) {
                                state.source = source
                                state.sorted = source.sortedArray()
                                state.lastResult = shownResult
                                state.analysis = results
                                state.busy = false
                                state.status = "Анализ готов"
                            }
                        }
                    },
                ) { Text("Анализ методов") }

                Button(
                    enabled = !state.busy,
                    onClick = { state.clear() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                    ),
                ) { Text("Очистка") }

                Button(
                    onClick = { exitProcess(0) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFB00020),
                        contentColor = Color.White,
                    ),
                ) { Text("Выход") }

                Spacer(modifier = Modifier.weight(1f))

                if (state.busy) {
                    CircularProgressIndicator(modifier = Modifier.width(20.dp))
                }
                state.status?.let { Text(it, style = MaterialTheme.typography.caption) }
            }
        }
    }
}

@Composable
private fun CounterField(label: String, value: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        singleLine = true,
        modifier = modifier,
    )
}
