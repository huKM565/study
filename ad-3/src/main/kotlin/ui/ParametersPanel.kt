package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import ru.hukm.A
import ru.hukm.B
import ru.hukm.N

@Composable
fun ParametersPanel(modifier: Modifier = Modifier) {
    Card(modifier = modifier, elevation = 2.dp) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Количество элементов массива", style = MaterialTheme.typography.caption)
            ReadOnlyField("n", N.toString())

            Text("Коэффициенты обратной функции", style = MaterialTheme.typography.caption)
            ReadOnlyField("A", A.toString())
            ReadOnlyField("B", B.toString())

            Text("Функция распределения Logistic", style = MaterialTheme.typography.caption)
            FormulaLine("F(x) = 1 / (1 + exp((A − x) / B)),   B > 0")

            Text("Обратная функция", style = MaterialTheme.typography.caption)
            FormulaLine("F⁻¹(u) = A − B · ln(1/u − 1)")
        }
    }
}

@Composable
private fun FormulaLine(text: String) {
    Text(
        text = text,
        fontFamily = FontFamily.Monospace,
        fontStyle = FontStyle.Italic,
        style = MaterialTheme.typography.body2,
    )
}

@Composable
private fun ReadOnlyField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}
