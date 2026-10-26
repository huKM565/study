package ru.hukm.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun Screen(content: @Composable () -> Unit) {
    MaterialTheme { Surface(Modifier.fillMaxSize()) { content() } }
}

@Composable
fun SubWindow(title: String, width: Int, height: Int, onClose: () -> Unit, content: @Composable () -> Unit) {
    Window(onCloseRequest = onClose, title = title, state = rememberWindowState(width = width.dp, height = height.dp)) {
        Screen(content)
    }
}

@Composable
fun Title(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier.fillMaxWidth(), fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
}

@Composable
fun Caption(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
fun Field(label: String, value: String, modifier: Modifier = Modifier, onChange: (String) -> Unit) {
    OutlinedTextField(value, onChange, modifier.fillMaxWidth(), label = { Text(label) }, singleLine = true)
}

@Composable
fun ReadOnlyField(label: String, value: String, modifier: Modifier = Modifier) {
    OutlinedTextField(value, {}, modifier.fillMaxWidth(), label = { Text(label) }, singleLine = true, readOnly = true)
}

@Composable
fun Formula(lines: List<String>, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)).padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(lines.joinToString("\n"), textAlign = TextAlign.Center, fontSize = 17.sp, lineHeight = 24.sp)
    }
}

@Composable
fun DataTable(
    headers: List<String>,
    weights: List<Float>,
    rowCount: Int,
    modifier: Modifier = Modifier,
    cell: (row: Int, column: Int) -> String,
) {
    OutlinedCard(modifier) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp)) {
                headers.forEachIndexed { column, header ->
                    Text(header, Modifier.weight(weights[column]), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            HorizontalDivider()
            LazyColumn(Modifier.fillMaxSize()) {
                items(rowCount) { row ->
                    Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 4.dp)) {
                        headers.indices.forEach { column ->
                            Text(cell(row, column), Modifier.weight(weights[column]), fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArrayTable(title: String, values: DoubleArray?, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Caption(title)
        DataTable(
            headers = listOf("Номер", "Элемент"),
            weights = listOf(0.7f, 1f),
            rowCount = values?.size ?: 0,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) { row, column -> if (column == 0) row.toString() else format(values!![row], 4) }
    }
}

@Composable
fun ErrorDialog(message: String?, onDismiss: () -> Unit) {
    message ?: return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Исходные данные") },
        text = { Text(message) },
        confirmButton = { TextButton(onDismiss) { Text("OK") } },
    )
}

fun format(value: Double, digits: Int): String =
    if (!value.isFinite()) value.toString()
    else BigDecimal(value).setScale(digits, RoundingMode.HALF_UP).toPlainString().replace('.', ',')

fun parseNumber(text: String): Double? = text.trim().replace(',', '.').toDoubleOrNull()

fun chooseFile(title: String, save: Boolean, defaultName: String): File? {
    val dialog = FileDialog(null as Frame?, title, if (save) FileDialog.SAVE else FileDialog.LOAD)
    dialog.directory = System.getProperty("user.home")
    dialog.file = defaultName
    dialog.isVisible = true
    val name = dialog.file ?: return null
    return File(dialog.directory, name)
}
