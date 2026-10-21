package ru.hukm.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.hukm.HashTable
import ru.hukm.SearchResult
import ru.hukm.Trip
import ru.hukm.binarySearch
import ru.hukm.getPopulatedTable
import ru.hukm.interpolationSearch
import ru.hukm.sequentialSearch

enum class Algo(val title: String, val complexity: String) {
    SEQUENTIAL("Последовательный", "O(n)"),
    BINARY("Двоичный", "O(log n)"),
    INTERPOLATION("Интерполяционный", "O(log log n)"),
    HASH("Хеш-таблица", "≈ O(1)"),
}

class Outcome(val key: Int, val byAlgo: Map<Algo, SearchResult>)

class AppState {
    var trips by mutableStateOf<List<Trip>>(getPopulatedTable())
        private set
    var selectedNumber by mutableStateOf<Int?>(null)
    private var hashTable = buildHashTable(trips)

    val selected: Trip? get() = trips.firstOrNull { it.number == selectedNumber }

    fun search(key: Int) = Outcome(
        key,
        mapOf(
            Algo.SEQUENTIAL to sequentialSearch(trips, key),
            Algo.BINARY to binarySearch(trips, key),
            Algo.INTERPOLATION to interpolationSearch(trips, key),
            Algo.HASH to hashTable.search(key),
        ),
    )

    fun contains(number: Int) = hashTable.search(number).trip != null

    fun add(trip: Trip) {
        trips = (trips + trip).sortedBy { it.number }
        hashTable.insert(trip)
    }

    fun remove(trip: Trip) {
        trips = trips - trip
        hashTable = buildHashTable(trips)
        if (selectedNumber == trip.number) selectedNumber = null
    }

    private fun buildHashTable(list: List<Trip>) = HashTable(211).apply { for (trip in list) insert(trip) }
}
