package ru.hukm

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlin.math.PI
import kotlin.math.abs

val OriginalColor = Color.Gray
val SampleColor = Color(0xFFD32F2F)
val RestoredColor = Color(0xFF1976D2)

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Дискретное преобразование сигналов", state = rememberWindowState(width = 1100.dp, height = 820.dp)) {
        MaterialTheme { Surface(Modifier.fillMaxSize()) { App() } }
    }
}

@Composable
fun App() {
    var um1Text by remember { mutableStateOf("9") }
    var um2Text by remember { mutableStateOf("5") }
    var f1Text by remember { mutableStateOf("5") }
    var f2Text by remember { mutableStateOf("9") }
    var phi1Text by remember { mutableStateOf("10") }
    var phi2Text by remember { mutableStateOf("40") }
    var kText by remember { mutableStateOf("1,25") }
    var durationText by remember { mutableStateOf("1") }
    var fMax by remember { mutableStateOf("") }
    var fd by remember { mutableStateOf("") }
    var td by remember { mutableStateOf("") }
    var sampleCount by remember { mutableStateOf("") }
    var maxError by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Conversion?>(null) }
    var duration by remember { mutableStateOf(0.0) }
    var error by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val um1 = parseNumber(um1Text)
        val um2 = parseNumber(um2Text)
        val f1 = parseNumber(f1Text)
        val f2 = parseNumber(f2Text)
        val phi1 = parseNumber(phi1Text)
        val phi2 = parseNumber(phi2Text)
        val k = parseNumber(kText)
        val durationMs = parseNumber(durationText)
        if (um1 == null || um2 == null || f1 == null || f2 == null || phi1 == null || phi2 == null || k == null || durationMs == null) {
            error = "Введите все исходные данные: числа или дроби."
            return
        }
        if (um1 <= 0.0 || um2 <= 0.0) {
            error = "Амплитуды должны быть больше 0."
            return
        }
        if (f1 <= 0.0 || f2 <= 0.0) {
            error = "Частоты должны быть больше 0."
            return
        }
        if (k <= 0.0) {
            error = "Коэффициент k должен быть больше 0."
            return
        }
        if (durationMs <= 0.0) {
            error = "Интервал наблюдения должен быть больше 0."
            return
        }
        val signal = Signal(um1, um2, f1 * 1000, f2 * 1000, phi1 * PI / 180, phi2 * PI / 180)
        val sampling = k * 2 * signal.fMax
        val seconds = durationMs / 1000
        if (seconds * sampling > 2000) {
            error = "Слишком много отсчётов на интервале: уменьшите интервал или k."
            return
        }
        val conversion = convert(signal, sampling, seconds)
        fMax = "${round4(signal.fMax / 1000)} кГц"
        fd = "${round4(conversion.fd / 1000)} кГц"
        td = "${round4(conversion.td * 1e6)} мкс"
        sampleCount = conversion.samples.size.toString()
        maxError = "${round4(conversion.maxError)} В"
        duration = seconds
        result = conversion
    }

    error?.let { message ->
        AlertDialog(
            onDismissRequest = { error = null },
            title = { Text("Исходные данные") },
            text = { Text(message) },
            confirmButton = { TextButton({ error = null }) { Text("OK") } },
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Group("Ввод исходных данных", Modifier.weight(1.3f)) {
                Formula("u(t) = Um1·cos(ω1·t + φ01) + Um2·cos(ω2·t + φ02)")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Field("Um1, В", um1Text) { um1Text = it }
                        Field("Um2, В", um2Text) { um2Text = it }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Field("F1, кГц", f1Text) { f1Text = it }
                        Field("F2, кГц", f2Text) { f2Text = it }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Field("φ01, град", phi1Text) { phi1Text = it }
                        Field("φ02, град", phi2Text) { phi2Text = it }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) { Field("k (fд = k·2Fmax)", kText) { kText = it } }
                    Box(Modifier.weight(1f)) { Field("Интервал, мс", durationText) { durationText = it } }
                    Button(::calculate, Modifier.weight(1f).height(56.dp)) { Text("Преобразовать") }
                }
            }
            Group("Вывод данных", Modifier.weight(1f)) {
                OutputRow("Верхняя частота Fmax", fMax)
                OutputRow("Частота дискретизации fд", fd)
                OutputRow("Период дискретизации Tд", td)
                OutputRow("Число отсчётов", sampleCount)
                OutputRow("Макс. погрешность восстановления", maxError)
            }
        }
        Group("График", Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Legend(OriginalColor, "Аналоговый сигнал")
                Legend(SampleColor, "Отсчёты")
                Legend(RestoredColor, "Восстановленный сигнал")
            }
            val conversion = result
            if (conversion == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нажмите «Преобразовать»", color = Color.Gray)
                }
            } else {
                Plot(conversion, duration, Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun Plot(conversion: Conversion, duration: Double, modifier: Modifier) {
    val measurer = rememberTextMeasurer()
    val labelStyle = TextStyle(fontSize = 12.sp, color = Color.DarkGray)
    Canvas(modifier) {
        val left = 60f
        val top = 10f
        val width = size.width - left - 20f
        val height = size.height - top - 30f
        val uMax = (conversion.original + conversion.restored).maxOf { abs(it.u) } * 1.1

        fun x(t: Double) = left + (t / duration * width).toFloat()
        fun y(u: Double) = top + (height / 2 * (1 - u / uMax)).toFloat()
        fun curve(points: List<Point>) = Path().apply {
            points.forEachIndexed { i, p -> if (i == 0) moveTo(x(p.t), y(p.u)) else lineTo(x(p.t), y(p.u)) }
        }

        drawLine(Color.LightGray, Offset(left, y(0.0)), Offset(left + width, y(0.0)))
        drawLine(Color.LightGray, Offset(left, top), Offset(left, top + height))
        drawLine(Color.LightGray, Offset(left, top + height), Offset(left + width, top + height))

        for (u in listOf(uMax, uMax / 2, 0.0, -uMax / 2, -uMax)) {
            val label = measurer.measure("%.1f В".format(u), labelStyle)
            drawText(label, topLeft = Offset(left - label.size.width - 6f, y(u) - label.size.height / 2))
        }
        for (i in 0..4) {
            val t = duration * i / 4
            val label = measurer.measure("${round4(t * 1000)} мс", labelStyle)
            drawText(label, topLeft = Offset(x(t) - label.size.width / 2, top + height + 6f))
        }

        drawPath(curve(conversion.original), OriginalColor, style = Stroke(2f))
        for (s in conversion.samples) {
            drawLine(SampleColor, Offset(x(s.t), y(0.0)), Offset(x(s.t), y(s.u)), strokeWidth = 1.5f)
            drawCircle(SampleColor, 4f, Offset(x(s.t), y(s.u)))
        }
        drawPath(curve(conversion.restored), RestoredColor, style = Stroke(2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))))
    }
}

@Composable
fun Legend(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(Modifier.size(width = 20.dp, height = 4.dp).background(color))
        Text(text, fontSize = 14.sp)
    }
}

@Composable
fun Group(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    OutlinedCard(modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
fun Field(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(value, onChange, Modifier.fillMaxWidth(), label = { Text(label) }, singleLine = true)
}

@Composable
fun Formula(text: String) {
    Box(Modifier.fillMaxWidth().height(56.dp).border(1.dp, Color.Gray), contentAlignment = Alignment.Center) {
        Text(text, textAlign = TextAlign.Center, fontSize = 16.sp)
    }
}

@Composable
fun OutputRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, Modifier.weight(1f), fontSize = 16.sp)
        OutlinedTextField(value, {}, Modifier.width(160.dp), readOnly = true, singleLine = true)
    }
}
