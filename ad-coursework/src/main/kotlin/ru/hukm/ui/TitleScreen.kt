package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleScreen(onTask: () -> Unit, onApp: () -> Unit, onExit: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Centered("МИНИСТЕРСТВО НАУКИ И ВЫСШЕГО ОБРАЗОВАНИЯ\nРОССИЙСКОЙ ФЕДЕРАЦИИ", 22.sp, FontWeight.Bold)
                Centered("федеральное государственное бюджетное образовательное учреждение\nвысшего образования", 17.sp)
                Centered("«Курганский государственный университет»", 18.sp, FontWeight.SemiBold)
                Centered("Кафедра программного обеспечения автоматизированных систем", 18.sp, FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Centered("КУРСОВАЯ РАБОТА", 38.sp, FontWeight.Bold)
                Centered("по дисциплине «Алгоритмы и структуры данных»", 22.sp, FontWeight.SemiBold)
            }
            Column(Modifier.widthIn(max = 640.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Signature("Выполнил студент группы ИТ-0930225", "Горных И. А.")
                Signature("Проверил преподаватель", "Семахин А. М.")
            }
            Centered("Курган 2026 г.", 18.sp, FontWeight.SemiBold)
        }
        Row(Modifier.widthIn(max = 900.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onTask, Modifier.weight(1f).height(52.dp)) { Text("Техническое задание") }
            Button(onApp, Modifier.weight(1f).height(52.dp)) { Text("Приложение") }
            OutlinedButton(onExit, Modifier.weight(1f).height(52.dp)) { Text("Выход") }
        }
    }
}

@Composable
private fun Centered(text: String, size: TextUnit, weight: FontWeight = FontWeight.Normal) {
    Text(text, fontSize = size, fontWeight = weight, textAlign = TextAlign.Center, lineHeight = size * 1.25f)
}

@Composable
private fun Signature(role: String, name: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(role, fontSize = 18.sp)
        Text(name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}
