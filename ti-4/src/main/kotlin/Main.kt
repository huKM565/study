package ru.hukm

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Оценка информационных характеристик систем", state = rememberWindowState(width = 760.dp, height = 720.dp)) {
        MaterialTheme { Surface(Modifier.fillMaxSize()) { App() } }
    }
}

@Composable
fun App() {
    var px1Text by remember { mutableStateOf("5/6") }
    var px2Text by remember { mutableStateOf("1/6") }
    var py1x1Text by remember { mutableStateOf("7/8") }
    var py2x2Text by remember { mutableStateOf("7/8") }
    var tauText by remember { mutableStateOf("0,1") }
    var performance by remember { mutableStateOf("") }
    var redundancy by remember { mutableStateOf("") }
    var transferRate by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val px1 = parseNumber(px1Text)
        val px2 = parseNumber(px2Text)
        val py1x1 = parseNumber(py1x1Text)
        val py2x2 = parseNumber(py2x2Text)
        val tau = parseNumber(tauText)
        if (px1 == null || px2 == null || py1x1 == null || py2x2 == null || tau == null) {
            error = "Введите все исходные данные: числа или дроби."
            return
        }
        if (listOf(px1, px2, py1x1, py2x2).any { it <= 0.0 || it > 1.0 }) {
            error = "Вероятности должны быть больше 0 и не больше 1."
            return
        }
        if (py1x1 != py2x2) {
            error = "Канал симметричный: p(y1/x1) и p(y2/x2) должны совпадать."
            return
        }
        if (tau <= 0.0) {
            error = "Длительность сигнала τ должна быть больше 0."
            return
        }
        val result = compute(px1, px2, py1x1, py2x2, tau)
        performance = "${round4(result.performance)} бит/с"
        redundancy = round4(result.redundancy).toString()
        transferRate = "${round4(result.transferRate)} бит/с"
        capacity = "${round4(result.capacity)} бит/с"
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
        Group("Ввод исходных данных") {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Field("p(x1)", px1Text) { px1Text = it }
                    Field("p(x2)", px2Text) { px2Text = it }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Field("p(y1/x1)", py1x1Text) { py1x1Text = it }
                    Field("p(y2/x2)", py2x2Text) { py2x2Text = it }
                }
                Box(Modifier.weight(0.6f)) {
                    Field("τ, с", tauText) { tauText = it }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Formula("Ī(X) = I(X) / τₓ = vₓ·I(X)")
                    Formula("R = 1 − H(X) / Hmax = 1 − I(X) / Imax")
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Formula("C = vₖ·log₂N")
                    Button(::calculate, Modifier.fillMaxWidth().height(56.dp)) { Text("Рассчитать") }
                }
            }
        }
        Group("Вывод данных") {
            OutputRow("Производительность источника", performance)
            OutputRow("Избыточность источника", redundancy)
            OutputRow("Скорость передачи информации", transferRate)
            OutputRow("Пропускная способность канала", capacity)
        }
    }
}

@Composable
fun Group(title: String, content: @Composable ColumnScope.() -> Unit) {
    OutlinedCard(Modifier.fillMaxWidth()) {
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
        OutlinedTextField(value, {}, Modifier.width(200.dp), readOnly = true, singleLine = true)
    }
}
