package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val STAGES = listOf(
    "Этап 1. Берётся элемент очередной позиции массива — начала цикла. Подсчитывается количество элементов правее него, которые меньше его. Это число определяет место элемента в упорядоченном массиве.",
    "Этап 2. Элемент записывается на найденное место (после равных ему), а вытесненный оттуда элемент становится текущим, и для него место ищется так же. Цикл продолжается, пока очередной элемент не встанет на начальную позицию.",
    "Этап 3. Этапы 1–2 повторяются для всех позиций массива, кроме последней. Каждый элемент записывается в массив не более одного раза, поэтому количество записей минимально.",
)

@Composable
fun AppScreen(onSort: () -> Unit, onVersions: () -> Unit, onRegression: () -> Unit, onExit: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Title("Алгоритм циклической сортировки Cycle sort (Б. К. Хэддон, 1990 г.)")
        OutlinedCard(Modifier.fillMaxWidth().weight(1f)) {
            Column(Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                for (stage in STAGES) Text(stage, fontSize = 18.sp, lineHeight = 26.sp)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onSort, Modifier.weight(1f).height(52.dp)) { Text("Упорядочивание данных") }
            Button(onVersions, Modifier.weight(1f).height(52.dp)) { Text("Сравнение версий") }
            Button(onRegression, Modifier.weight(1f).height(52.dp)) { Text("Уравнение связи") }
            OutlinedButton(onExit, Modifier.weight(1f).height(52.dp)) { Text("Выход") }
        }
    }
}
