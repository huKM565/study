package ru.hukm

import kotlin.math.ln
import kotlin.math.round
import kotlin.random.Random

const val N = 19000
const val A = 50.0
const val B = 10.0

class Method(val name: String, val sort: (DoubleArray, Counter) -> Unit)

class Result(val method: Method, val timeMs: Double, val counter: Counter)

val methods = listOf(
    Method("Простой выбор", ::selectionSort),
    Method("Простая вставка", ::insertionSort),
    Method("Простой обмен", ::bubbleSort),
    Method("Шелла", ::shellSort),
    Method("Хоара", ::quickSort),
    Method("Пирамиды", ::heapSort),
)

fun analyze(source: DoubleArray): List<Result> {
    for (method in methods) measure(method, generateArray())
    val results = methods.map { measure(it, source) }
    return results
}

fun logistic(): Double {
    while (true) {
        val u = Random.nextDouble()
        val x = A - B * ln(1 / u - 1)
        if (x in 0.0..100.0) return x
    }
}

fun generateArray() = DoubleArray(N) { logistic() }

fun measure(method: Method, source: DoubleArray): Result {
    val array = source.copyOf()
    val counter = Counter()
    val start = System.nanoTime()
    method.sort(array, counter)
    val timeMs = (System.nanoTime() - start) / 1_000_000.0
    check(isSorted(array)) { "${method.name}: массив не отсортирован" }
    return Result(method, timeMs, counter)
}

fun isSorted(a: DoubleArray): Boolean {
    for (i in 1 until a.size) {
        if (a[i - 1] > a[i]) return false
    }
    return true
}

fun round2(x: Double) = round(x * 100) / 100
