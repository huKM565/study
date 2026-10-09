package ru.hukm

import kotlin.math.log2
import kotlin.math.round

class Entropies(val hx: Double, val hy: Double, val hz: Double)

fun parseProbability(text: String): Double? {
    val t = text.trim().replace(',', '.')
    val value = if ('/' in t) {
        val parts = t.split('/')
        if (parts.size != 2) return null
        val numerator = parts[0].trim().toDoubleOrNull() ?: return null
        val denominator = parts[1].trim().toDoubleOrNull() ?: return null
        if (denominator == 0.0) return null
        numerator / denominator
    } else {
        t.toDoubleOrNull() ?: return null
    }
    return if (value > 0.0 && value < 1.0) value else null
}

fun entropy(vararg p: Double): Double {
    var h = 0.0
    for (pi in p) h -= pi * log2(pi)
    return h
}

fun compute(px1: Double, px2: Double, px3: Double): Entropies {
    val px23 = px2 + px3
    val hx = entropy(px1, px2, px3)
    val hy = entropy(px1, px23)
    val hz = entropy(px2 / px23, px3 / px23)
    return Entropies(hx, hy, hz)
}

fun round4(x: Double) = round(x * 10000) / 10000
