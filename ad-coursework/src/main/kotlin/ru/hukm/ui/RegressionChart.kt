package ru.hukm.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hukm.Regression
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

private fun niceStep(max: Double): Double {
    val raw = max / 5
    val power = 10.0.pow(floor(log10(raw)))
    val fraction = raw / power
    val nice = when {
        fraction <= 1 -> 1.0
        fraction <= 2 -> 2.0
        fraction <= 5 -> 5.0
        else -> 10.0
    }
    return nice * power
}

private fun digits(step: Double): Int = if (step >= 1) 0 else ceil(-log10(step)).toInt()

private val POINT = Color(0xFFD32F2F)
private val LINE = Color(0xFF2E7D32)
private val ESTIMATE = Color(0xFF1565C0)

@Composable
fun RegressionChart(x: DoubleArray, y: DoubleArray, regression: Regression?, modifier: Modifier = Modifier) {
    val measurer = rememberTextMeasurer()
    val grid = MaterialTheme.colorScheme.outlineVariant
    val axis = MaterialTheme.colorScheme.onSurface
    val labelStyle = TextStyle(fontSize = 12.sp, color = axis)

    OutlinedCard(modifier) {
        Canvas(Modifier.fillMaxSize().padding(16.dp)) {
            val left = 56f
            val right = 12f
            val top = 12f
            val bottom = 30f
            val width = size.width - left - right
            val height = size.height - top - bottom
            val stepX = niceStep(x.maxOrNull() ?: 1.0)
            val stepY = niceStep(y.maxOrNull() ?: 1.0)
            val countX = ceil((x.maxOrNull() ?: 1.0) / stepX).toInt().coerceAtLeast(1)
            val countY = ceil((y.maxOrNull() ?: 1.0) / stepY).toInt().coerceAtLeast(1)
            val maxX = stepX * countX
            val maxY = stepY * countY
            fun px(value: Double) = left + (value / maxX * width).toFloat()
            fun py(value: Double) = top + height - (value / maxY * height).toFloat()

            for (i in 0..countX) {
                val gx = stepX * i
                drawLine(grid, Offset(px(gx), top), Offset(px(gx), top + height))
                val label = measurer.measure(format(gx, digits(stepX)), labelStyle)
                drawText(label, topLeft = Offset(px(gx) - label.size.width / 2, top + height + 6))
            }
            for (i in 0..countY) {
                val gy = stepY * i
                drawLine(grid, Offset(left, py(gy)), Offset(left + width, py(gy)))
                val label = measurer.measure(format(gy, digits(stepY)), labelStyle)
                drawText(label, topLeft = Offset(left - label.size.width - 6, py(gy) - label.size.height / 2))
            }
            drawLine(axis, Offset(left, top + height), Offset(left + width, top + height), 2f)
            drawLine(axis, Offset(left, top), Offset(left, top + height), 2f)

            clipRect(left, top, left + width, top + height) {
                regression?.let { r ->
                    drawLine(LINE, Offset(px(0.0), py(r.predict(0.0))), Offset(px(maxX), py(r.predict(maxX))), 3f)
                    for (value in x) drawCircle(ESTIMATE, 4f, Offset(px(value), py(r.predict(value))))
                }
                for (i in x.indices) drawCircle(POINT, 5f, Offset(px(x[i]), py(y[i])))
            }
        }
    }
}
