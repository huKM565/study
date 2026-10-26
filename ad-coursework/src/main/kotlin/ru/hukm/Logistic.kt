package ru.hukm

import kotlin.math.exp
import kotlin.math.ln
import kotlin.random.Random

const val DEFAULT_N = 5000
const val DEFAULT_A = 50.0
const val DEFAULT_B = 10.0

fun logisticCdf(x: Double, a: Double, b: Double): Double = 1 / (1 + exp((a - x) / b))

fun logisticInverse(u: Double, a: Double, b: Double): Double = a - b * ln(1 / u - 1)

fun generateLogistic(n: Int, a: Double, b: Double, random: Random = Random.Default): DoubleArray =
    DoubleArray(n) {
        var u = random.nextDouble()
        while (u == 0.0) u = random.nextDouble()
        logisticInverse(u, a, b)
    }
