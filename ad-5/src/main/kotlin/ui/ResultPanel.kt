package ru.hukm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hukm.DijkstraResult
import ru.hukm.NODE_COUNT
import ru.hukm.START_NODE
import ru.hukm.pathTo

@Composable
fun ResultPanel(
    result: DijkstraResult?,
    selected: Int?,
    onCalculate: () -> Unit,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier, shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Кратчайшие пути от узла $START_NODE", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Button(onClick = onCalculate, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text("Рассчитать")
            }

            if (result == null) return@Column

            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                HeadCell("Узел", .6f)
                HeadCell("Метка", 1f)
                HeadCell("Путь", 1.8f)
            }
            for (node in 1..NODE_COUNT) {
                val label = result.labels[node]
                val path = pathTo(result, node)
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(if (node == selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .clickable { onSelect(node) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Cell(node.toString(), .6f, bold = true)
                    Cell(label?.toString() ?: "—", 1f)
                    Cell(if (path.isEmpty()) "нет пути" else path.joinToString(" → "), 1.8f)
                }
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
    )
}

@Composable
private fun RowScope.Cell(text: String, weight: Float, bold: Boolean = false) {
    Text(
        text,
        Modifier.weight(weight),
        fontSize = 14.sp,
        fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
    )
}
