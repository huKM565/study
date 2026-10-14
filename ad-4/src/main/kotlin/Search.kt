package ru.hukm

class SearchResult(val trip: Trip?, val comparisons: Int)

fun sequentialSearch(table: List<Trip>, key: Int): SearchResult {
    var comparisons = 0
    for (trip in table) {
        comparisons++
        if (trip.number == key) return SearchResult(trip, comparisons)
    }
    return SearchResult(null, comparisons)
}

fun binarySearch(table: List<Trip>, key: Int): SearchResult {
    var comparisons = 0
    var left = 0
    var right = table.size - 1
    while (left <= right) {
        val middle = (left + right) / 2
        comparisons++
        when {
            table[middle].number == key -> return SearchResult(table[middle], comparisons)
            table[middle].number < key -> left = middle + 1
            else -> right = middle - 1
        }
    }
    return SearchResult(null, comparisons)
}

fun interpolationSearch(table: List<Trip>, key: Int): SearchResult {
    var comparisons = 0
    var left = 0
    var right = table.size - 1
    while (left <= right && key >= table[left].number && key <= table[right].number) {
        val low = table[left].number
        val high = table[right].number
        val position = if (high == low) left else left + (key - low) * (right - left) / (high - low)
        comparisons++
        when {
            table[position].number == key -> return SearchResult(table[position], comparisons)
            table[position].number < key -> left = position + 1
            else -> right = position - 1
        }
    }
    return SearchResult(null, comparisons)
}

class HashTable(size: Int) {
    private val cells = arrayOfNulls<Trip>(size)

    fun hash(key: Int) = key % cells.size

    fun insert(trip: Trip) {
        var index = hash(trip.number)
        while (cells[index] != null) index = (index + 1) % cells.size
        cells[index] = trip
    }

    fun search(key: Int): SearchResult {
        var comparisons = 0
        var index = hash(key)
        while (cells[index] != null) {
            comparisons++
            if (cells[index]!!.number == key) return SearchResult(cells[index], comparisons)
            index = (index + 1) % cells.size
        }
        return SearchResult(null, comparisons)
    }
}
