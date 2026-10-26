package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private class Block(val formulas: List<String>, val notes: List<String>, val fraction: Boolean = false)

private val TOP = listOf(
    Block(
        listOf("S = Σ (yᵢ − ŷᵢ)² → min"),
        listOf(
            "где yᵢ — наблюдаемое значение эндогенной переменной;",
            "ŷᵢ — расчётное значение эндогенной переменной;",
            "i — номер наблюдения, i = 1, …, m;",
            "m — объём выборки.",
        ),
    ),
    Block(
        listOf("ŷ = a₀ + a₁ · x"),
        listOf(
            "где ŷ — эндогенная переменная (время сортировки);",
            "x — экзогенная переменная, x = n² / 10⁶, так как T(n) = O(n²);",
            "a₀ — свободный коэффициент;",
            "a₁ — выборочный коэффициент.",
        ),
    ),
    Block(
        listOf("Sx = √( Σ (xᵢ − x̄)² / m )", "Sy = √( Σ (yᵢ − ȳ)² / m )"),
        listOf(
            "где x̄ — среднее арифметическое значение выборки X;",
            "ȳ — среднее арифметическое значение выборки Y;",
            "m — объём выборки.",
        ),
    ),
)

private val BOTTOM = listOf(
    Block(
        listOf(
            "∂S/∂a₀ = −2 · Σ (yᵢ − a₀ − a₁ · xᵢ) = 0",
            "∂S/∂a₁ = −2 · Σ (yᵢ − a₀ − a₁ · xᵢ) · xᵢ = 0",
            "",
            "m · a₀ + a₁ · Σ xᵢ = Σ yᵢ",
            "a₀ · Σ xᵢ + a₁ · Σ xᵢ² = Σ xᵢ · yᵢ",
        ),
        emptyList(),
    ),
    Block(listOf(), emptyList(), fraction = true),
    Block(
        listOf(
            "x̄ = Σ xᵢ / m,   ȳ = Σ yᵢ / m",
            "",
            "Э = a₁ · x̄ / ȳ",
            "",
            "β = a₁ · Sx / Sy",
        ),
        emptyList(),
    ),
)

@Composable
fun TheoryScreen(onExit: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Title("Метод наименьших квадратов")
        BlockRow(TOP, Modifier.weight(1f))
        BlockRow(BOTTOM, Modifier.weight(1.1f))
        OutlinedButton(onExit, Modifier.fillMaxWidth().height(52.dp)) { Text("Выход") }
    }
}

@Composable
private fun BlockRow(blocks: List<Block>, modifier: Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        for (block in blocks) {
            OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
                Column(
                    Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                ) {
                    if (block.fraction) Correlation()
                    else Text(block.formulas.joinToString("\n"), textAlign = TextAlign.Center, fontSize = 18.sp, lineHeight = 26.sp)
                    if (block.notes.isNotEmpty()) {
                        Text(block.notes.joinToString("\n"), Modifier.fillMaxWidth(), fontSize = 15.sp, lineHeight = 21.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun Correlation() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("r = ", fontSize = 17.sp)
        Column(Modifier.width(IntrinsicSize.Max), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("m · Σ xᵢ · yᵢ − Σ xᵢ · Σ yᵢ", fontSize = 16.sp)
            HorizontalDivider(Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.onSurface)
            Text("√((m · Σ xᵢ² − (Σ xᵢ)²) · (m · Σ yᵢ² − (Σ yᵢ)²))", fontSize = 16.sp)
        }
    }
    Text("R² = r²", fontSize = 18.sp)
}
