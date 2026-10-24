package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hukm.Platform

@Composable
fun PathPanel(state: AppState) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                PlatformDropdown("Откуда", state.source, state.platforms, Modifier.weight(1f)) { state.source = it }
                PlatformDropdown("Куда", state.target, state.platforms, Modifier.weight(1f)) { state.target = it }
                TextButton(onClick = {
                    val a = state.source
                    state.source = state.target
                    state.target = a
                }) { Text("⇄") }
            }

            val total = state.totalDistance(state.source, state.target)
            val legs = state.legs(state.source, state.target)

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    when {
                        state.source.id == state.target.id -> Text("Выбраны одна и та же платформа — расстояние 0 км.")
                        total == null -> Text(
                            "Путь между ${state.source.name} и ${state.target.name} не найден.",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold,
                        )
                        else -> {
                            Text(
                                "Кратчайшее расстояние: $total км",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                legs.joinToString(" → ", prefix = "${state.source.id}. ${state.source.name} → ") {
                                    "${it.to.id}. ${it.to.name}"
                                },
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .75f),
                            )
                        }
                    }
                }
            }

            if (legs.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Разбивка по участкам", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
                    legs.forEachIndexed { index, leg ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("${index + 1}.", Modifier.padding(end = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f))
                            Text("${leg.from.id} → ${leg.to.id}", Modifier.weight(.4f), fontWeight = FontWeight.SemiBold)
                            Text("${leg.distance} км", Modifier.weight(.4f))
                            Text("итого: ${leg.cumulative} км", Modifier.weight(.6f), color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlatformDropdown(
    label: String,
    selected: Platform,
    platforms: List<Platform>,
    modifier: Modifier = Modifier,
    onSelect: (Platform) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = "${selected.id}. ${selected.name}",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            platforms.forEach { platform ->
                DropdownMenuItem(
                    text = { Text("${platform.id}. ${platform.name}") },
                    onClick = {
                        onSelect(platform)
                        expanded = false
                    },
                )
            }
        }
    }
}
