package ru.hukm.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hukm.Trip

private object W {
    const val NUMBER = .6f
    const val ROUTE = 2.8f
    const val DEPARTURE = .8f
    const val BUS = 1.5f
    const val SEATS = .6f
    const val DRIVER = 1.5f
    const val PRICE = .9f
}

@Composable
fun TripsTable(
    trips: List<Trip>,
    selectedNumber: Int?,
    listState: LazyListState,
    query: String,
    onQuery: (String) -> Unit,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQuery,
                    singleLine = true,
                    placeholder = { Text("Фильтр: номер, маршрут, автобус или водитель") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                )
                Text("${trips.size} записей", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f))
            }

            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 14.dp, vertical = 9.dp),
            ) {
                HeadCell("№", W.NUMBER)
                HeadCell("Маршрут", W.ROUTE)
                HeadCell("Отпр.", W.DEPARTURE)
                HeadCell("Автобус", W.BUS)
                HeadCell("Мест", W.SEATS)
                HeadCell("Водитель", W.DRIVER)
                HeadCell("Цена", W.PRICE)
            }

            Box(Modifier.weight(1f)) {
                if (trips.isEmpty()) {
                    Text(
                        "Ничего не найдено",
                        Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f),
                    )
                }
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(end = 10.dp)) {
                    items(trips, key = { it.number }) { trip ->
                        TripRow(trip, trip.number == selectedNumber) { onSelect(trip.number) }
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
private fun RowScope.Cell(text: String, weight: Float, color: Color = Color.Unspecified, bold: Boolean = false) {
    Text(
        text,
        Modifier.weight(weight).padding(end = 8.dp),
        color = color,
        fontSize = 14.sp,
        fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun TripRow(trip: Trip, selected: Boolean, onClick: () -> Unit) {
    val muted = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f)
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Cell(trip.number.toString(), W.NUMBER, bold = true)
        Cell(trip.route, W.ROUTE)
        Cell(trip.departure, W.DEPARTURE)
        Cell(trip.bus, W.BUS, muted)
        Cell(trip.seats.toString(), W.SEATS, muted)
        Cell(trip.driver, W.DRIVER, muted)
        Cell("${trip.price} ₽", W.PRICE)
    }
}
