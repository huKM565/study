package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import ru.hukm.DEFAULT_A
import ru.hukm.DEFAULT_B
import ru.hukm.DEFAULT_N
import ru.hukm.SortData
import ru.hukm.SortResult
import ru.hukm.Version
import ru.hukm.generateLogistic
import ru.hukm.loadSortData
import ru.hukm.runSort
import ru.hukm.saveSortData
import ru.hukm.warmUp

const val MAX_SORT_SIZE = 100_000

@Composable
fun SortScreen(onExit: () -> Unit) {
    val scope = rememberCoroutineScope()
    var nText by remember { mutableStateOf(DEFAULT_N.toString()) }
    var aText by remember { mutableStateOf(format(DEFAULT_A, 1)) }
    var bText by remember { mutableStateOf(format(DEFAULT_B, 1)) }
    var source by remember { mutableStateOf<DoubleArray?>(null) }
    var result by remember { mutableStateOf<SortResult?>(null) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun reset() {
        source = null
        result = null
    }

    fun sort() {
        val n = nText.trim().toIntOrNull()
        val a = parseNumber(aText)
        val b = parseNumber(bText)
        if (n == null || a == null || b == null) {
            error = "Введите n целым числом, A и B — числами."
            return
        }
        if (n < 2 || n > MAX_SORT_SIZE) {
            error = "Объём массива n должен быть от 2 до $MAX_SORT_SIZE."
            return
        }
        if (b <= 0) {
            error = "Параметр B должен быть больше 0."
            return
        }
        busy = true
        val current = source
        scope.launch {
            val (array, sorted) = withContext(Dispatchers.Default) {
                warmUp()
                val array = current ?: generateLogistic(n, a, b)
                array to runSort(Version.CLASSIC, array, a, b)
            }
            source = array
            result = sorted
            busy = false
        }
    }

    fun save() {
        val array = source
        if (array == null) {
            error = "Нет данных для сохранения."
            return
        }
        val file = chooseFile("Сохранение данных", save = true, defaultName = "sort.txt") ?: return
        saveSortData(file, SortData(array.size, parseNumber(aText) ?: DEFAULT_A, parseNumber(bText) ?: DEFAULT_B, array, result))
    }

    fun load() {
        val file = chooseFile("Загрузка данных", save = false, defaultName = "sort.txt") ?: return
        val data = runCatching { loadSortData(file) }.getOrElse {
            error = "Файл не содержит данных сортировки."
            return
        }
        nText = data.n.toString()
        aText = format(data.a, 1)
        bText = format(data.b, 1)
        source = data.source
        result = data.result
    }

    ErrorDialog(error) { error = null }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Title("Сортировка данных")
        Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Field("n", nText) { nText = it; reset() }
                Field("A", aText) { aText = it; reset() }
                Field("B", bText) { bText = it; reset() }
                Caption("ВЗР Logistic", Modifier.padding(top = 8.dp))
                Formula(listOf("F(x) = 1 / (1 + exp((A − x) / B))"))
                Formula(listOf("F⁻¹(u) = A − B · ln(1 / u − 1)"))
            }
            Column(Modifier.weight(2f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    ArrayTable("Исходный массив", source, Modifier.weight(1f).fillMaxHeight())
                    ArrayTable("Упорядоченный массив", result?.sorted, Modifier.weight(1f).fillMaxHeight())
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    ReadOnlyField("Время, мс", result?.let { format(it.millis, 3) }.orEmpty(), Modifier.weight(1f))
                    ReadOnlyField("Количество сравнений", result?.comparisons?.toString().orEmpty(), Modifier.weight(1f))
                    ReadOnlyField("Количество перестановок", result?.writes?.toString().orEmpty(), Modifier.weight(1f))
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Button(::sort, Modifier.weight(1f).height(52.dp), enabled = !busy) { Text("Сортировка") }
            OutlinedButton(::reset, Modifier.weight(1f).height(52.dp), enabled = !busy) { Text("Очистка") }
            OutlinedButton(::save, Modifier.weight(1f).height(52.dp), enabled = !busy) { Text("Сохранить") }
            OutlinedButton(::load, Modifier.weight(1f).height(52.dp), enabled = !busy) { Text("Загрузить") }
            OutlinedButton(onExit, Modifier.weight(1f).height(52.dp)) { Text("Выход") }
        }
    }
}
