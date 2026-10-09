package ru.hukm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

val background = Color(0xFFF0F0F0)

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Оценка энтропийных характеристик", state = rememberWindowState(width = 1400.dp, height = 640.dp)) {
        MaterialTheme { Surface(Modifier.fillMaxSize(), color = background) { App() } }
    }
}

@Composable
fun App() {
    var px1Text by remember { mutableStateOf("9/23") }
    var px2Text by remember { mutableStateOf("8/23") }
    var px3Text by remember { mutableStateOf("5/23") }
    var hx by remember { mutableStateOf("") }
    var hy by remember { mutableStateOf("") }
    var hz by remember { mutableStateOf("") }
    var showInputError by remember { mutableStateOf(false) }

    fun calculate() {
        val px1 = parseProbability(px1Text)
        val px2 = parseProbability(px2Text)
        val px3 = parseProbability(px3Text)
        if (px1 == null || px2 == null || px3 == null) {
            showInputError = true
            return
        }
        val result = compute(px1, px2, px3)
        hx = round4(result.hx).toString()
        hy = round4(result.hy).toString()
        hz = round4(result.hz).toString()
    }

    if (showInputError) AlertDialog(
        onDismissRequest = { showInputError = false },
        title = { Text("Исходные данные") },
        text = { Text("Введите вероятности Px1, Px2, Px3: числа или дроби от 0 до 1.") },
        confirmButton = { TextButton({ showInputError = false }) { Text("OK") } },
    )

    Row(Modifier.fillMaxSize().padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(Modifier.weight(0.47f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InputRow("Px1", px1Text, { px1Text = it }, "Энтропия для X", hx, "Px = (Px1, Px2, Px3)ᵀ")
            InputRow("Px2", px2Text, { px2Text = it }, "Энтропия для Y", hy, "Py = (Px1, Px2 + Px3)ᵀ")
            InputRow("Px3", px3Text, { px3Text = it }, "Энтропия для Z", hz, "Pz = (Px2/(Px2 + Px3), Px3/(Px2 + Px3))ᵀ")
            Button(::calculate, Modifier.width(150.dp)) { Text("Вычислить") }
            Column(
                Modifier.fillMaxWidth().weight(1f).background(Color.White).padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
            ) {
                Formula("H(X) = Σ p(xᵢ)·log2(1/p(xᵢ)) = −Σ p(xᵢ)·log2 p(xᵢ),   i = 1..N")
                Formula("H(X) = Px1·log2(Px1) + Px2·log2(Px2) + Px3·log2(Px3)")
                Formula("H(Y) = Px1·log2(Px1) + (Px2 + Px3)·log2(Px2 + Px3)")
                Formula("H(Z) = (Px2/(Px2 + Px3))·log2(Px2/(Px2 + Px3)) + (Px3/(Px2 + Px3))·log2(Px3/(Px2 + Px3))")
            }
        }

        Column(Modifier.weight(0.53f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FormulaBox("H(Y) − (Px2 + Px3)·log2(Px2 + Px3) = Px1·log2(Px1)")
            FormulaBox("H(X) = H(Y) − (Px2 + Px3)·log2(Px2 + Px3) + Px2·log2(Px2) + Px3·log2(Px3)")
            FormulaBox("H(Z)·(Px2 + Px3) = Px2·(log2(Px2) − log2(Px2 + Px3)) + Px3·(log2(Px3) − log2(Px2 + Px3))")
            FormulaBox("H(Z)·(Px2 + Px3) = Px2·log2(Px2) + Px3·log2(Px3) − (Px2 + Px3)·log2(Px2 + Px3)")
            Text("Связь между энтропиями", fontSize = 12.sp)
            FormulaBox("H(X) = H(Y) + (Px2 + Px3)·H(Z)")
        }
    }
}

@Composable
fun InputRow(inputLabel: String, input: String, onInput: (String) -> Unit, outputLabel: String, output: String, vector: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(inputLabel, Modifier.width(30.dp), fontSize = 13.sp)
        OutlinedTextField(input, onInput, Modifier.width(100.dp), singleLine = true)
        Text(outputLabel, Modifier.width(110.dp), fontSize = 13.sp)
        OutlinedTextField(output, {}, Modifier.width(100.dp), readOnly = true, singleLine = true)
        FormulaBox(vector, Modifier.weight(1f))
    }
}

@Composable
fun Formula(text: String) = Text(text, Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 15.sp)

@Composable
fun FormulaBox(text: String, modifier: Modifier = Modifier.fillMaxWidth()) {
    Box(modifier.height(70.dp).background(Color.White).padding(8.dp), contentAlignment = Alignment.Center) {
        Text(text, textAlign = TextAlign.Center, fontSize = 15.sp)
    }
}
