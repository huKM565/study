package ru.hukm

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.round
import kotlin.math.sin

class Signal(
    val um1: Double,
    val um2: Double,
    val f1: Double,
    val f2: Double,
    val phi1: Double,
    val phi2: Double,
) {
    val fMax get() = max(f1, f2)

    fun u(t: Double) = um1 * cos(2 * PI * f1 * t + phi1) + um2 * cos(2 * PI * f2 * t + phi2)
}

class Point(val t: Double, val u: Double)

class Conversion(
    val fd: Double,
    val td: Double,
    val original: List<Point>,
    val samples: List<Point>,
    val restored: List<Point>,
    val maxError: Double,
)

fun parseNumber(text: String): Double? {
    val t = text.trim().replace(',', '.')
    if ('/' !in t) return t.toDoubleOrNull()
    val parts = t.split('/')
    if (parts.size != 2) return null
    val numerator = parts[0].trim().toDoubleOrNull() ?: return null
    val denominator = parts[1].trim().toDoubleOrNull() ?: return null
    if (denominator == 0.0) return null
    return numerator / denominator
}

fun sinc(x: Double) = if (x == 0.0) 1.0 else sin(x) / x

// Ряд Котельникова бесконечный, поэтому отсчёты берутся и за краями интервала,
// иначе обрезка ряда искажает восстановленный сигнал у краёв графика
const val MARGIN = 1000

fun convert(signal: Signal, fd: Double, duration: Double, points: Int = 1000): Conversion {
    val td = 1 / fd
    val n = floor(duration / td).toInt()
    val allSamples = (-MARGIN..n + MARGIN).map { k -> Point(k * td, signal.u(k * td)) }

    val dt = duration / points
    val original = (0..points).map { i -> Point(i * dt, signal.u(i * dt)) }
    val restored = original.map { p ->
        Point(p.t, allSamples.sumOf { s -> s.u * sinc(PI * fd * (p.t - s.t)) })
    }
    val maxError = original.indices.maxOf { abs(original[it].u - restored[it].u) }

    return Conversion(
        fd = fd,
        td = td,
        original = original,
        samples = allSamples.filter { it.t in 0.0..duration },
        restored = restored,
        maxError = maxError,
    )
}

fun round4(x: Double) = round(x * 10000) / 10000
