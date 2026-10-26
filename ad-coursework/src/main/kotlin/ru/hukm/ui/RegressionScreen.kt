package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hukm.DEFAULT_SAMPLE
import ru.hukm.Observation
import ru.hukm.loadObservations
import ru.hukm.regression
import ru.hukm.runSeries
import ru.hukm.saveObservations

const val MIN_SAMPLE = 3
const val MAX_SAMPLE = 100

@Composable
fun RegressionScreen(onTheory: () -> Unit, onExit: () -> Unit) {
    val scope = rememberCoroutineScope()
    var mText by remember { mutableStateOf(DEFAULT_SAMPLE.toString()) }
    var observations by remember { mutableStateOf<List<Observation>>(emptyList()) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val x = observations.map { it.x }.toDoubleArray()
    val y = observations.map { it.millis }.toDoubleArray()
    val result = if (observations.size >= 2) regression(x, y) else null

    fun calculate() {
        val m = mText.trim().toIntOrNull()
        if (m == null || m < MIN_SAMPLE || m > MAX_SAMPLE) {
            error = "Объём выборки должен быть целым числом от $MIN_SAMPLE до $MAX_SAMPLE."
            return
        }
        busy = true
        scope.launch {
            observations = withContext(Dispatchers.Default) { runSeries(m) }
            busy = false
        }
    }

    fun save() {
        if (observations.isEmpty()) {
            error = "Нет данных для сохранения."
            return
        }
        val file = chooseFile("Сохранение данных", save = true, defaultName = "series.txt") ?: return
        saveObservations(file, observations)
    }

    fun load() {
        val file = chooseFile("Загрузка данных", save = false, defaultName = "series.txt") ?: return
        val loaded = runCatching { loadObservations(file) }.getOrElse {
            error = "Файл не содержит статистических данных."
            return
        }
        observations = loaded
        mText = loaded.size.toString()
    }

    fun value(number: Double?, digits: Int) = number?.let { format(it, digits) }.orEmpty()

    ErrorDialog(error) { error = null }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Title("Оценивание коэффициентов уравнения связи")
        Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(
                Modifier.weight(1.15f).fillMaxHeight().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Field("Объём выборки", mText, Modifier.width(220.dp)) { mText = it }
                Caption("Исходные статистические данные")
                DataTable(
                    headers = listOf("Номер", "n", "x = n² / 10⁶", "Время y, мс", "x²", "x·y"),
                    weights = listOf(0.6f, 0.8f, 1f, 1f, 1.1f, 1.1f),
                    rowCount = observations.size,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                ) { row, column ->
                    val o = observations[row]
                    when (column) {
                        0 -> (row + 1).toString()
                        1 -> o.n.toString()
                        2 -> format(o.x, 4)
                        3 -> format(o.millis, 3)
                        4 -> format(o.x * o.x, 2)
                        else -> format(o.x * o.millis, 2)
                    }
                }
                if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                Caption("Система нормальных уравнений")
                Equation(result?.m?.toString().orEmpty(), value(result?.sumX, 4), value(result?.sumY, 4))
                Equation(value(result?.sumX, 4), value(result?.sumXX, 4), value(result?.sumXY, 4))
                Caption("Коэффициенты уравнения связи")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ReadOnlyField("a0", value(result?.a0, 6), Modifier.weight(1f))
                    ReadOnlyField("a1", value(result?.a1, 6), Modifier.weight(1f))
                }
                ReadOnlyField("Уравнение связи", result?.let { "y = ${format(it.a0, 6)} + ${format(it.a1, 6)} · x" }.orEmpty())
            }
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Caption("Точечный график и график уравнения связи")
                RegressionChart(x, y, result, Modifier.fillMaxWidth().weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ReadOnlyField("Коэффициент корреляции", value(result?.correlation, 6), Modifier.weight(1f))
                    ReadOnlyField("Коэффициент детерминации", value(result?.determination, 6), Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ReadOnlyField("Коэффициент эластичности", value(result?.elasticity, 6), Modifier.weight(1f))
                    ReadOnlyField("Бета-коэффициент", value(result?.beta, 6), Modifier.weight(1f))
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(::calculate, Modifier.weight(1f).height(52.dp), enabled = !busy) { Text("Расчёт") }
            OutlinedButton({ observations = emptyList() }, Modifier.weight(1f).height(52.dp), enabled = !busy) { Text("Очистка") }
            OutlinedButton(::save, Modifier.weight(1f).height(52.dp), enabled = !busy) { Text("Сохранить") }
            OutlinedButton(::load, Modifier.weight(1f).height(52.dp), enabled = !busy) { Text("Загрузить") }
            OutlinedButton(onTheory, Modifier.weight(1f).height(52.dp)) { Text("МНК") }
            OutlinedButton(onExit, Modifier.weight(1f).height(52.dp)) { Text("Выход") }
        }
    }
}

@Composable
private fun Equation(first: String, second: String, third: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ValueBox(first, Modifier.weight(1f))
        Text("a0 +", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        ValueBox(second, Modifier.weight(1f))
        Text("a1 =", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        ValueBox(third, Modifier.weight(1f))
    }
}

@Composable
private fun ValueBox(text: String, modifier: Modifier = Modifier) {
    OutlinedTextField(text, {}, modifier, singleLine = true, readOnly = true)
}
