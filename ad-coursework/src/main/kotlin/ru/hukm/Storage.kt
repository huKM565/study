package ru.hukm

import java.io.File

class SortData(val n: Int, val a: Double, val b: Double, val source: DoubleArray, val result: SortResult?)

fun saveSortData(file: File, data: SortData) {
    val lines = mutableListOf(
        "n=${data.n}",
        "A=${data.a}",
        "B=${data.b}",
        "Исходный массив=${data.source.joinToString(";")}",
    )
    data.result?.let {
        lines += "Время=${it.millis}"
        lines += "Сравнения=${it.comparisons}"
        lines += "Перестановки=${it.writes}"
        lines += "Упорядоченный массив=${it.sorted.joinToString(";")}"
    }
    file.writeText(lines.joinToString("\n"))
}

fun loadSortData(file: File): SortData {
    val values = readValues(file)
    val result = values["Упорядоченный массив"]?.let {
        SortResult(
            sorted = parseArray(it),
            millis = values.getValue("Время").toDouble(),
            comparisons = values.getValue("Сравнения").toLong(),
            writes = values.getValue("Перестановки").toLong(),
        )
    }
    return SortData(
        n = values.getValue("n").toInt(),
        a = values.getValue("A").toDouble(),
        b = values.getValue("B").toDouble(),
        source = parseArray(values.getValue("Исходный массив")),
        result = result,
    )
}

fun saveObservations(file: File, observations: List<Observation>) {
    file.writeText(
        listOf(
            "m=${observations.size}",
            "n=${observations.joinToString(";") { it.n.toString() }}",
            "Время=${observations.joinToString(";") { it.millis.toString() }}",
        ).joinToString("\n"),
    )
}

fun loadObservations(file: File): List<Observation> {
    val values = readValues(file)
    val sizes = values.getValue("n").split(";").map { it.toInt() }
    val times = parseArray(values.getValue("Время"))
    return sizes.indices.map { Observation(sizes[it], times[it]) }
}

private fun readValues(file: File): Map<String, String> =
    file.readLines().filter { "=" in it }.associate { it.substringBefore("=") to it.substringAfter("=") }

private fun parseArray(text: String): DoubleArray =
    if (text.isEmpty()) DoubleArray(0) else text.split(";").map { it.toDouble() }.toDoubleArray()
