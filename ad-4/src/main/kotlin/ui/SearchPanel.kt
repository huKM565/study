package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Green = Color(0xFF16A34A)

@Composable
fun SearchPanel(
    keyText: String,
    onKey: (String) -> Unit,
    outcome: Outcome?,
    onSearch: () -> Unit,
    onAdd: (Int) -> Unit,
) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = keyText,
                    onValueChange = onKey,
                    label = { Text("Номер рейса") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(200.dp).onPreviewKeyEvent {
                        if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                            onSearch()
                            true
                        } else {
                            false
                        }
                    },
                )
                Button(onClick = onSearch, shape = RoundedCornerShape(12.dp), modifier = Modifier.height(52.dp)) {
                    Text("Найти", fontSize = 15.sp)
                }
            }

            if (outcome != null) {
                val found = outcome.byAlgo.getValue(Algo.SEQUENTIAL).trip != null
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (found) {
                        Text("Найден рейс № ${outcome.key}", color = Green, fontWeight = FontWeight.SemiBold)
                    } else {
                        Text(
                            "Рейс № ${outcome.key} не найден",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.width(12.dp))
                        FilledTonalButton(onClick = { onAdd(outcome.key) }, shape = RoundedCornerShape(10.dp)) {
                            Text("Добавить рейс № ${outcome.key}")
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Algo.entries.forEach { a ->
                        val result = outcome.byAlgo.getValue(a)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f),
                        ) {
                            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text("${a.title} · ${a.complexity}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f))
                                Text("${result.comparisons} сравн.", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
