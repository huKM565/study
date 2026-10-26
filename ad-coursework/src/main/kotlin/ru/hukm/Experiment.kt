package ru.hukm

import kotlin.random.Random

enum class Version(val title: String, val complexity: String) {
    CLASSIC("Классический Cycle sort", "O(n²)"),
    RANKED("Места через сортировку индексов", "O(n log n)"),
    LOGISTIC("Места через корзины по F(x)", "O(n)"),
}

class SortResult(val sorted: DoubleArray, val millis: Double, val comparisons: Long, val writes: Long)

fun runSort(version: Version, source: DoubleArray, a: Double, b: Double): SortResult {
    val array = source.copyOf()
    val begin = System.nanoTime()
    val stats = when (version) {
        Version.CLASSIC -> cycleSort(array)
        Version.RANKED -> cycleSortRanked(array)
        Version.LOGISTIC -> cycleSortLogistic(array, a, b)
    }
    val millis = (System.nanoTime() - begin) / 1_000_000.0
    return SortResult(array, millis, stats.comparisons, stats.writes)
}

private var warmedUp = false

fun warmUp() {
    if (warmedUp) return
    repeat(5) {
        val source = generateLogistic(3000, DEFAULT_A, DEFAULT_B)
        for (version in Version.entries) runSort(version, source, DEFAULT_A, DEFAULT_B)
    }
    warmedUp = true
}

val COMPARISON_SIZES = listOf(1_000, 10_000, 100_000, 1_000_000)
const val CLASSIC_LIMIT = 100_000

class ComparisonRow(val n: Int, val millis: Map<Version, Double?>)

fun compareVersions(): List<ComparisonRow> {
    warmUp()
    return COMPARISON_SIZES.map { n ->
        val source = generateLogistic(n, DEFAULT_A, DEFAULT_B)
        val millis = Version.entries.associateWith { version ->
            if (version == Version.CLASSIC && n > CLASSIC_LIMIT) null
            else runSort(version, source, DEFAULT_A, DEFAULT_B).millis
        }
        ComparisonRow(n, millis)
    }
}

const val DEFAULT_SAMPLE = 25
const val MIN_SIZE = 1_000
const val MAX_SIZE = 20_000

class Observation(val n: Int, val millis: Double) {
    val x: Double get() = n.toDouble() * n / 1_000_000
}

fun runSeries(m: Int, random: Random = Random.Default): List<Observation> {
    warmUp()
    return List(m) { random.nextInt(MIN_SIZE, MAX_SIZE + 1) }.sorted().map { n ->
        val source = generateLogistic(n, DEFAULT_A, DEFAULT_B, random)
        Observation(n, runSort(Version.CLASSIC, source, DEFAULT_A, DEFAULT_B).millis)
    }
}
