package ru.hukm

val table = getPopulatedTable()
val hashTable = HashTable(97).apply { for (trip in table) insert(trip) }

fun main() {
    printWelcomeMessage()
    while (true) {
        print("> ")
        val input = readlnOrNull()?.trim() ?: return

        if (input.isEmpty()) {
            printError("ввод пустой")
            continue
        }

        val parts = input.split(" ").filter { it.isNotBlank() }
        val action = parts[0].toIntOrNull()

        if (action == null) {
            printError("номер действия не число")
            continue
        }

        if (action !in 0..6) {
            printError("номер действия не соответствует пунктам меню")
            continue
        }

        when (action) {
            0 -> return
            1 -> printAll()
            2 -> search(parts, "Последовательный поиск") { sequentialSearch(table, it) }
            3 -> search(parts, "Двоичный поиск") { binarySearch(table, it) }
            4 -> search(parts, "Интерполяционный поиск") { interpolationSearch(table, it) }
            5 -> search(parts, "Поиск в хеш-таблице") { hashTable.search(it) }
            6 -> addTrip()
        }
    }
}

fun printAll() {
    printHeader("Рейсы ОАО «Автовокзал» (${table.size})")
    println("№".padEnd(6) + "Маршрут".padEnd(43) + "Отпр.".padEnd(7) + "Автобус".padEnd(18) + "Мест".padEnd(6) + "Водитель".padEnd(16) + "Цена")
    for (trip in table) {
        println(
            trip.number.toString().padEnd(6) + trip.route.padEnd(43) + trip.departure.padEnd(7) +
                trip.bus.padEnd(18) + trip.seats.toString().padEnd(6) + trip.driver.padEnd(16) + trip.price
        )
    }
}

fun search(parts: List<String>, methodName: String, method: (Int) -> SearchResult) {
    val key = parts.getOrNull(1)?.toIntOrNull()
    if (key == null) {
        printError("укажите номер рейса числом, например: ${parts[0]} 130")
        return
    }
    val result = method(key)
    printHeader("$methodName, сравнений: ${result.comparisons}")
    if (result.trip == null) {
        println("Рейс № $key не найден. Добавить рейс — пункт 6")
    } else {
        println(result.trip)
    }
}

fun addTrip() {
    val number = readNumber("Номер рейса: ") ?: return
    if (hashTable.search(number).trip != null) {
        printError("рейс № $number уже есть")
        return
    }
    val route = readText("Маршрут: ") ?: return
    val departure = readText("Время отправления: ") ?: return
    val bus = readText("Автобус: ") ?: return
    val seats = readNumber("Количество мест: ") ?: return
    val driver = readText("Водитель: ") ?: return
    val price = readNumber("Цена билета, руб.: ") ?: return

    val trip = Trip(number, route, departure, bus, seats, driver, price)
    val index = table.indexOfFirst { it.number > number }
    table.add(if (index == -1) table.size else index, trip)
    hashTable.insert(trip)
    println("Рейс № $number добавлен")
}

fun readText(prompt: String): String? {
    print(prompt)
    val text = readlnOrNull()?.trim()
    if (text.isNullOrEmpty()) {
        printError("ввод пустой")
        return null
    }
    return text
}

fun readNumber(prompt: String): Int? {
    val text = readText(prompt) ?: return null
    val value = text.toIntOrNull()
    if (value == null || value <= 0) {
        printError("нужно положительное целое число")
        return null
    }
    return value
}

fun printHeader(title: String) {
    println(title)
    println("-".repeat(50))
}

fun printError(message: String) {
    println("Ошибка: $message")
}

fun printWelcomeMessage() {
    println("ЛР4, вариант 9")
    println("Поиск в таблице рейсов ОАО «Автовокзал»")
    println("  1          - вывод всех рейсов")
    println("  2 <номер>  - последовательный поиск")
    println("  3 <номер>  - двоичный поиск")
    println("  4 <номер>  - интерполяционный поиск")
    println("  5 <номер>  - поиск в хеш-таблице")
    println("  6          - добавить рейс")
    println("  0          - выход")
}
