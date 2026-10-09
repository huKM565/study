package ru.hukm.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.hukm.round2

@Composable
fun AnalysisTable(state: AppState, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), elevation = 2.dp) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Text(
                "Анализ методов сортировки",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold,
            )

            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 2.dp)) {
                HeaderCell("Метод", weight = 0.4f)
                HeaderCell("Время, мс", weight = 0.2f)
                HeaderCell("Сравнения", weight = 0.2f)
                HeaderCell("Перестановки", weight = 0.2f)
            }
            Divider()

            val listState = rememberLazyListState()
            Box(modifier = Modifier.fillMaxSize()) {
                if (state.analysis.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("—", color = Color.Gray)
                    }
                } else {
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                        items(count = state.analysis.size) { index ->
                            val r = state.analysis[index]
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .background(if (index % 2 == 0) Color(0xFFF5F7FA) else Color.Transparent)
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Cell(r.method.name, weight = 0.4f, mono = false)
                                Cell(round2(r.timeMs).toString(), weight = 0.2f)
                                Cell(r.counter.comparisons.toString(), weight = 0.2f)
                                Cell(r.counter.moves.toString(), weight = 0.2f)
                            }
                        }
                    }
                    VerticalScrollbar(
                        adapter = rememberScrollbarAdapter(listState),
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.HeaderCell(text: String, weight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(weight).padding(horizontal = 4.dp),
        style = MaterialTheme.typography.caption,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun RowScope.Cell(text: String, weight: Float, mono: Boolean = true) {
    Text(
        text = text,
        modifier = Modifier.weight(weight).padding(horizontal = 4.dp),
        fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default,
        style = MaterialTheme.typography.body2,
    )
}
