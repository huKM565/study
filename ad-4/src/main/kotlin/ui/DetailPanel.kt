package ru.hukm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hukm.Trip

@Composable
fun DetailPanel(trip: Trip?, onDelete: (Trip) -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        if (trip == null) {
            Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(
                    "Выберите рейс в таблице\nили найдите его по номеру",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f),
                )
            }
            return@Card
        }
        val stops = trip.route.split(" — ")
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Рейс № ${trip.number}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(trip.route, fontSize = 16.sp, fontWeight = FontWeight.Medium)

            HorizontalDivider()

            Text("Пункты маршрута", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                stops.forEachIndexed { index, stop ->
                    val role = when (index) {
                        0 -> "отправление"
                        stops.lastIndex -> "конечный"
                        else -> "промежуточный"
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            Modifier.size(10.dp).background(
                                if (index == 0 || index == stops.lastIndex) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                                CircleShape,
                            ),
                        )
                        Text(stop, Modifier.weight(1f), fontSize = 14.sp)
                        Text(role, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f))
                    }
                }
            }

            HorizontalDivider()

            InfoRow("Отправление", trip.departure)
            InfoRow("Автобус", trip.bus)
            InfoRow("Мест", trip.seats.toString())
            InfoRow("Водитель", trip.driver)
            InfoRow("Цена билета", "${trip.price} ₽")

            TextButton(onClick = { onDelete(trip) }, modifier = Modifier.fillMaxWidth()) {
                Text("Удалить рейс", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
