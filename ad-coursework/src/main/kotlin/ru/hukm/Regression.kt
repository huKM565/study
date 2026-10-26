package ru.hukm

import kotlin.math.sqrt

class Regression(
    val m: Int,
    val sumX: Double,
    val sumY: Double,
    val sumXX: Double,
    val sumXY: Double,
    val a0: Double,
    val a1: Double,
    val correlation: Double,
    val determination: Double,
    val elasticity: Double,
    val beta: Double,
) {
    fun predict(x: Double): Double = a0 + a1 * x
}

fun regression(x: DoubleArray, y: DoubleArray): Regression {
    val m = x.size
    val sumX = x.sum()
    val sumY = y.sum()
    val sumXX = x.sumOf { it * it }
    val sumYY = y.sumOf { it * it }
    val sumXY = x.indices.sumOf { x[it] * y[it] }
    val a1 = (m * sumXY - sumX * sumY) / (m * sumXX - sumX * sumX)
    val a0 = (sumY - a1 * sumX) / m
    val correlation = (m * sumXY - sumX * sumY) /
        sqrt((m * sumXX - sumX * sumX) * (m * sumYY - sumY * sumY))
    val meanX = sumX / m
    val meanY = sumY / m
    val sx = sqrt(x.sumOf { (it - meanX) * (it - meanX) } / m)
    val sy = sqrt(y.sumOf { (it - meanY) * (it - meanY) } / m)
    return Regression(
        m = m,
        sumX = sumX,
        sumY = sumY,
        sumXX = sumXX,
        sumXY = sumXY,
        a0 = a0,
        a1 = a1,
        correlation = correlation,
        determination = correlation * correlation,
        elasticity = a1 * meanX / meanY,
        beta = a1 * sx / sy,
    )
}
