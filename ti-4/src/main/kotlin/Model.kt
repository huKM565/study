package ru.hukm

import kotlin.math.log2
import kotlin.math.round

class Characteristics(
    val performance: Double,
    val redundancy: Double,
    val transferRate: Double,
    val capacity: Double,
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

fun plog(p: Double) = if (p == 0.0) 0.0 else p * log2(p)

fun compute(px1: Double, px2: Double, py1x1: Double, py2x2: Double, tau: Double): Characteristics {
    val px = doubleArrayOf(px1, px2)
    val pyx = arrayOf(
        doubleArrayOf(py1x1, 1 - py1x1),
        doubleArrayOf(1 - py2x2, py2x2),
    )
    val py = DoubleArray(2) { j -> px[0] * pyx[0][j] + px[1] * pyx[1][j] }

    val hx = -(plog(px1) + plog(px2))
    val hMax = log2(2.0)

    var iyx = 0.0
    for (i in 0..1) {
        for (j in 0..1) {
            val pxy = px[i] * pyx[i][j]
            if (pxy > 0.0) iyx += pxy * log2(pxy / (px[i] * py[j]))
        }
    }

    val v = 1 / tau
    val p0 = 1 - py1x1
    val capacity = v * (log2(2.0) + plog(p0) + plog(1 - p0))

    return Characteristics(
        performance = v * hx,
        redundancy = 1 - hx / hMax,
        transferRate = v * iyx,
        capacity = capacity,
    )
}

fun round4(x: Double) = round(x * 10000) / 10000
