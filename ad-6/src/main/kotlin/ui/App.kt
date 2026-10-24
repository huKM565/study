package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun App() {
    val state = remember { AppState() }

    Scaffold { padding ->
        Row(
            Modifier.padding(padding).fillMaxSize().padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(Modifier.weight(1.1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Header()
                PathPanel(state)
                EdgesPanel(state.edges, state.platforms, Modifier.weight(1f))
            }
            MatrixPanel(state, Modifier.weight(1.6f).fillMaxHeight())
        }
    }
}

@Composable
private fun Header() {
    Column {
        Text("BP Sea Company", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(
            "Кратчайшие пути между нефтедобывающими платформами",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
        )
    }
}
