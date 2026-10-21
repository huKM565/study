package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun App() {
    val state = remember { AppState() }
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var keyText by remember { mutableStateOf("") }
    var outcome by remember { mutableStateOf<Outcome?>(null) }
    var query by remember { mutableStateOf("") }
    var scrollTick by remember { mutableStateOf(0) }
    var addDialogNumber by remember { mutableStateOf<Int?>(null) }

    fun toast(message: String) {
        scope.launch { snack.showSnackbar(message) }
    }

    fun runSearch() {
        val key = keyText.trim().toIntOrNull()
        if (key == null) {
            toast("Введите номер рейса — целое число")
            return
        }
        val result = state.search(key)
        outcome = result
        val found = result.byAlgo.getValue(Algo.SEQUENTIAL).trip
        if (found != null) {
            state.selectedNumber = found.number
            query = ""
            scrollTick++
        }
    }

    val visible = remember(state.trips, query) {
        state.trips.filter { trip ->
            query.isBlank() || trip.number.toString().contains(query) || trip.route.contains(query, true) ||
                trip.driver.contains(query, true) || trip.bus.contains(query, true)
        }
    }

    LaunchedEffect(scrollTick) {
        if (scrollTick > 0) {
            val index = visible.indexOfFirst { it.number == state.selectedNumber }
            if (index >= 0) listState.animateScrollToItem(maxOf(0, index - 2))
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snack) }) { padding ->
        Row(
            Modifier.padding(padding).fillMaxSize().padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Header()
                SearchPanel(
                    keyText = keyText,
                    onKey = { keyText = it.filter(Char::isDigit).take(9) },
                    outcome = outcome,
                    onSearch = ::runSearch,
                    onAdd = { addDialogNumber = it },
                )
                TripsTable(
                    trips = visible,
                    selectedNumber = state.selectedNumber,
                    listState = listState,
                    query = query,
                    onQuery = { query = it },
                    onSelect = { state.selectedNumber = it },
                    modifier = Modifier.weight(1f),
                )
            }
            DetailPanel(
                trip = state.selected,
                onDelete = { trip ->
                    state.remove(trip)
                    outcome = null
                    toast("Рейс № ${trip.number} удалён")
                },
                modifier = Modifier.weight(0.45f).widthIn(max = 380.dp).fillMaxHeight(),
            )
        }
    }

    addDialogNumber?.let { number ->
        AddTripDialog(
            initialNumber = number,
            isTaken = state::contains,
            onDismiss = { addDialogNumber = null },
            onSave = { trip ->
                state.add(trip)
                state.selectedNumber = trip.number
                outcome = null
                query = ""
                scrollTick++
                addDialogNumber = null
                toast("Рейс № ${trip.number} добавлен")
            },
        )
    }
}

@Composable
fun Header() {
    Text("Рейсы ОАО «Автовокзал»", fontSize = 26.sp, fontWeight = FontWeight.Bold)
}
