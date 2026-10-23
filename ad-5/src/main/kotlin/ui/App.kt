package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hukm.DijkstraResult
import ru.hukm.NODE_COUNT
import ru.hukm.START_NODE
import ru.hukm.dijkstra
import ru.hukm.edges
import ru.hukm.pathTo

@Composable
fun App() {
    var result by remember { mutableStateOf<DijkstraResult?>(null) }
    var selected by remember { mutableStateOf<Int?>(null) }
    val path = result?.let { r -> selected?.let { pathTo(r, it) } }.orEmpty()

    Surface(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxSize().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Транспортная сеть филиалов торговой фирмы", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Card(
                    Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(1.dp),
                ) {
                    GraphView(edges, path, Modifier.fillMaxSize().padding(16.dp))
                }
            }
            ResultPanel(
                result = result,
                selected = selected,
                onCalculate = {
                    result = dijkstra(NODE_COUNT, edges, START_NODE)
                    selected = null
                },
                onSelect = { selected = it },
                modifier = Modifier.weight(0.75f).widthIn(max = 460.dp).fillMaxHeight(),
            )
        }
    }
}
