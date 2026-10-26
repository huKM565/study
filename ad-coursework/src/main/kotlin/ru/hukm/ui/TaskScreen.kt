package ru.hukm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TASK = listOf(
    "1 Назначение, цели и задачи разработки" to listOf(
        "Проектируемая программная система предназначена для анализа алгоритма сортировки данных Cycle sort (циклическая сортировка) при обработке массивов, элементы которых распределены по логистическому закону распределения случайных величин.",
        "Основная учебная цель — повышение теоретических знаний и практических навыков в разработке, анализе и программной реализации алгоритмов решения задач и выборе структур данных.",
        "Основные задачи разработки:",
        "– разработка алгоритма сортировки Cycle sort и обоснование выбора структур данных;",
        "– анализ алгоритма и разработка его улучшенных версий;",
        "– программная реализация генератора случайных величин по закону Logistic методом обратной функции;",
        "– программная реализация алгоритма;",
        "– проведение экспериментального исследования алгоритма и анализ его результатов методом наименьших квадратов;",
        "– документирование курсовой работы в соответствии с установленными требованиями.",
    ),
    "2 Основания для разработки" to listOf(
        "Учебный план направления 09.03.03 «Прикладная информатика».",
        "Рабочая программа дисциплины «Алгоритмы и структуры данных».",
        "Задание на курсовую работу, вариант 9.",
        "Плановые сроки выполнения — осенний семестр 2026/27 учебного года.",
    ),
    "3 Требования к функциональным характеристикам" to listOf(
        "Проектируемая система должна обеспечивать выполнение следующих функций:",
        "– ввод исходных данных: объём массива n, параметры закона распределения A и B, объём выборки m;",
        "– генерация массива случайных величин по закону Logistic;",
        "– сортировка массива методом Cycle sort;",
        "– вывод исходного и упорядоченного массивов;",
        "– оценка сложности реализации алгоритма по временным (время сортировки) и объёмным (количество сравнений и перестановок) параметрам;",
        "– сравнение версий алгоритма;",
        "– оценивание коэффициентов уравнения связи методом наименьших квадратов, расчёт коэффициентов корреляции, детерминации, эластичности и бета-коэффициента;",
        "– хранение исходных данных и результатов расчётов с возможностью их загрузки для повторной обработки.",
    ),
    "4 Требования к эксплуатационным характеристикам" to listOf(
        "– модульность: генератор случайных величин, алгоритм сортировки, экспериментальное исследование, метод наименьших квадратов, хранение данных и интерфейс выполнены отдельными модулями;",
        "– расширяемость: подключение новых версий алгоритма и законов распределения без переработки остальных модулей.",
    ),
    "5 Требования к программному обеспечению" to listOf(
        "– язык программирования Kotlin;",
        "– библиотека графического интерфейса Compose Multiplatform (Desktop);",
        "– среда разработки IntelliJ IDEA, система сборки Gradle;",
        "– операционная система Windows, Linux или macOS с JDK 21.",
    ),
    "6 Стадии и этапы разработки" to listOf(
        "Эскизный проект: обзор методов сортировки данных; описание алгоритма Cycle sort; описание закона распределения Logistic; разработка методики экспериментального исследования.",
        "Технический проект: разработка структуры программного комплекса; разработка структур и форм представления данных.",
        "Рабочий проект: программная реализация; тестирование и отладка; подготовка программной и эксплуатационной документации.",
        "Эксплуатация: проведение экспериментального исследования, описание и анализ его результатов.",
    ),
    "7 Требования к документированию" to listOf(
        "К защите представляются визуальное приложение и альбом документов: опись альбома, пояснительная записка, спецификация, описание программы, руководство пользователя, руководство программиста, текст программы (на машинном носителе).",
        "Требования к структуре документов определены стандартами ЕСПД.",
    ),
    "8 Порядок контроля и приёмки" to listOf(
        "Контроль выполнения курсовой работы проводится руководителем поэтапно в соответствии с графиком выполнения.",
        "Защита проводится комиссией; исполнитель представляет документацию, делает сообщение по теме и демонстрирует программную реализацию.",
    ),
)

@Composable
fun TaskScreen(onExit: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Title("Сортировка данных. Метод Cycle sort.\nВероятностный закон распределения случайных величин «Logistic»")
        OutlinedCard(Modifier.fillMaxWidth().weight(1f)) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("ТЕХНИЧЕСКОЕ ЗАДАНИЕ", Modifier.fillMaxWidth(), fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(
                    "на курсовую работу по дисциплине «Алгоритмы и структуры данных»",
                    Modifier.fillMaxWidth(),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )
                for ((heading, paragraphs) in TASK) {
                    Text(heading, Modifier.padding(top = 8.dp), fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    for (paragraph in paragraphs) Text(paragraph, fontSize = 15.sp)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            OutlinedButton(onExit, Modifier.width(240.dp).height(52.dp)) { Text("Выход") }
        }
    }
}
