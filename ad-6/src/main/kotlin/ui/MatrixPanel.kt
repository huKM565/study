package ru.hukm.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hukm.INF

@Composable
fun MatrixPanel(state: AppState, modifier: Modifier = Modifier) {
    val horizontal = rememberScrollState()
    val vertical = rememberScrollState()

    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Матрицы алгоритма Флойда", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MatrixView.entries.forEach { view ->
                        FilterChip(
                            selected = state.matrixView == view,
                            onClick = { state.matrixView = view },
                            label = { Text(view.title) },
                        )
                    }
                }
            }

            val source = state.source.id
            val target = state.target.id
            val onPath = state.floyd.path(source, target).toSet()

            Box(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxSize().verticalScroll(vertical).horizontalScroll(horizontal)) {
                    MatrixHeader(state.platforms.map { it.id })
                    state.platforms.forEach { row ->
                        MatrixRow(
                            rowId = row.id,
                            values = state.platforms.map { col ->
                                when (state.matrixView) {
                                    MatrixView.DISTANCE -> renderDistance(state.floyd.distance[row.id - 1][col.id - 1], row.id == col.id)
                                    MatrixView.SEQUENCE -> renderSequence(state.floyd.sequence[row.id - 1][col.id - 1], row.id == col.id)
                                }
                            },
                            isActiveRow = row.id == source,
                            targetCol = target,
                            highlightCol = { col -> col == source || col == target || col in onPath },
                        )
                    }
                }
                VerticalScrollbar(rememberScrollbarAdapter(vertical), Modifier.align(Alignment.CenterEnd).fillMaxHeight())
            }

            Text(
                when (state.matrixView) {
                    MatrixView.DISTANCE -> "D[i][j] — длина кратчайшего пути из i в j. «∞» — пути нет, «—» — главная диагональ."
                    MatrixView.SEQUENCE -> "S[i][j] — промежуточный узел k, через который прошёл треугольный оператор. «—» — прямое ребро либо диагональ."
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f),
            )
        }
    }
}

@Composable
private fun MatrixHeader(ids: List<Int>) {
    Row {
        Cell("i\\j", header = true)
        ids.forEach { Cell(it.toString(), header = true) }
    }
}

@Composable
private fun MatrixRow(
    rowId: Int,
    values: List<String>,
    isActiveRow: Boolean,
    targetCol: Int,
    highlightCol: (Int) -> Boolean,
) {
    val rowBg = if (isActiveRow) MaterialTheme.colorScheme.primaryContainer.copy(alpha = .35f) else Color.Transparent
    Row(Modifier.background(rowBg)) {
        Cell(rowId.toString(), header = true)
        values.forEachIndexed { index, text ->
            val colId = index + 1
            val isTarget = isActiveRow && colId == targetCol
            Cell(
                text = text,
                highlighted = highlightCol(colId) || isActiveRow,
                focus = isTarget,
            )
        }
    }
}

@Composable
private fun Cell(
    text: String,
    header: Boolean = false,
    highlighted: Boolean = false,
    focus: Boolean = false,
) {
    val bg = when {
        focus -> MaterialTheme.colorScheme.primary.copy(alpha = .22f)
        header -> MaterialTheme.colorScheme.surfaceVariant
        highlighted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .4f)
        else -> Color.Transparent
    }
    Box(
        Modifier.width(60.dp).height(34.dp).padding(1.dp).clip(RoundedCornerShape(6.dp)).background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = if (header || focus) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = if (focus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun renderDistance(value: Int, diagonal: Boolean): String = when {
    diagonal -> "—"
    value >= INF -> "∞"
    else -> value.toString()
}

private fun renderSequence(value: Int, diagonal: Boolean): String = when {
    diagonal -> "—"
    value == -1 -> "—"
    else -> (value + 1).toString()
}
