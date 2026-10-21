package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.hukm.Trip

private val timeFormat = Regex("""\d{1,2}:\d{2}""")

@Composable
private fun Field(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    error: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        label = { Text(label) },
        singleLine = true,
        isError = error != null,
        supportingText = if (error != null) ({ Text(error) }) else null,
        shape = RoundedCornerShape(12.dp),
    )
}

@Composable
fun AddTripDialog(initialNumber: Int, isTaken: (Int) -> Boolean, onDismiss: () -> Unit, onSave: (Trip) -> Unit) {
    var numberText by remember { mutableStateOf(initialNumber.toString()) }
    var route by remember { mutableStateOf("") }
    var departure by remember { mutableStateOf("") }
    var bus by remember { mutableStateOf("") }
    var seatsText by remember { mutableStateOf("") }
    var driver by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }

    val number = numberText.toIntOrNull()
    val seats = seatsText.toIntOrNull()
    val price = priceText.toIntOrNull()
    val numberError = when {
        number == null || number <= 0 -> "Введите положительное число"
        isTaken(number) -> "Номер уже занят"
        else -> null
    }
    val departureError = if (departure.isNotBlank() && !timeFormat.matches(departure.trim())) "Формат ЧЧ:ММ" else null
    val valid = numberError == null && route.isNotBlank() && departure.isNotBlank() && departureError == null &&
        bus.isNotBlank() && driver.isNotBlank() && seats != null && seats > 0 && price != null && price > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый рейс") },
        text = {
            Column(Modifier.width(420.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Field("Номер рейса", numberText, { numberText = it.filter(Char::isDigit).take(9) }, error = numberError)
                Field("Маршрут (пункты через « — »)", route, { route = it })
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Field("Отправление", departure, { departure = it }, Modifier.weight(1f), departureError)
                    Field("Мест", seatsText, { seatsText = it.filter(Char::isDigit).take(3) }, Modifier.weight(1f))
                }
                Field("Автобус", bus, { bus = it })
                Field("Водитель", driver, { driver = it })
                Field("Цена билета, ₽", priceText, { priceText = it.filter(Char::isDigit).take(6) })
            }
        },
        confirmButton = {
            Button(
                enabled = valid,
                onClick = {
                    onSave(Trip(number!!, route.trim(), departure.trim(), bus.trim(), seats!!, driver.trim(), price!!))
                },
            ) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}
