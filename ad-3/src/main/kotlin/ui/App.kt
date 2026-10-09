package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    val state = remember { AppState() }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ParametersPanel(modifier = Modifier.fillMaxHeight().width(260.dp))
            ArrayTable(
                title = "Исходный массив",
                data = state.source,
                modifier = Modifier.fillMaxHeight().weight(1f),
            )
            ArrayTable(
                title = "Упорядоченный массив",
                data = state.sorted,
                modifier = Modifier.fillMaxHeight().weight(1f),
            )
            Column(
                modifier = Modifier.fillMaxHeight().weight(1.4f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MethodSelector(state)
                AnalysisTable(state, modifier = Modifier.weight(1f))
            }
        }
        BottomPanel(state)
    }
}
