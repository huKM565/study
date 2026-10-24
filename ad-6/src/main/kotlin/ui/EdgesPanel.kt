package ru.hukm.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hukm.Edge
import ru.hukm.Platform

@Composable
fun EdgesPanel(edges: List<Edge>, platforms: List<Platform>, modifier: Modifier = Modifier) {
    val byId = platforms.associateBy { it.id }
    val sorted = edges.sortedWith(compareBy({ it.from }, { it.to }))
    val listState = rememberLazyListState()

    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Рёбра графа", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text("${edges.size} рёбер · ${platforms.size} узлов", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f))
            }
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 14.dp, vertical = 9.dp),
            ) {
                HeadCell("i", .3f)
                HeadCell("Откуда", 1.6f)
                HeadCell("j", .3f)
                HeadCell("Куда", 1.6f)
                HeadCell("Длина, км", .9f)
            }
            Box(Modifier.fillMaxSize()) {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(end = 10.dp)) {
                    items(sorted) { edge ->
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Cell(edge.from.toString(), .3f, bold = true)
                            Cell(byId[edge.from]?.name.orEmpty(), 1.6f)
                            Cell(edge.to.toString(), .3f, bold = true)
                            Cell(byId[edge.to]?.name.orEmpty(), 1.6f)
                            Cell("${edge.distance}", .9f, bold = true)
                        }
                    }
                }
                VerticalScrollbar(rememberScrollbarAdapter(listState), Modifier.align(Alignment.CenterEnd).fillMaxHeight())
            }
        }
    }
}

@Composable
private fun RowScope.HeadCell(text: String, weight: Float) {
    Text(
        text.uppercase(),
        Modifier.weight(weight),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f),
        maxLines = 1,
    )
}

@Composable
private fun RowScope.Cell(text: String, weight: Float, bold: Boolean = false) {
    Text(
        text,
        Modifier.weight(weight),
        fontSize = 14.sp,
        fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
        maxLines = 1,
    )
}
