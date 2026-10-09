package ru.hukm

class Counter {
    var comparisons = 0L
    var moves = 0L
}

fun swap(a: DoubleArray, i: Int, j: Int) {
    val t = a[i]
    a[i] = a[j]
    a[j] = t
}

// Простой выбор
fun selectionSort(a: DoubleArray, counter: Counter) {
    for (last in a.size - 1 downTo 1) {
        var max = 0
        for (i in 1..last) {
            counter.comparisons++
            if (a[i] > a[max]) max = i
        }
        if (max != last) {
            swap(a, max, last)
            counter.moves++
        }
    }
}

// Простая вставка
fun insertionSort(a: DoubleArray, counter: Counter) {
    for (i in 1 until a.size) {
        val key = a[i]
        var j = i - 1
        while (j >= 0) {
            counter.comparisons++
            if (a[j] <= key) break
            a[j + 1] = a[j]
            counter.moves++
            j--
        }
        a[j + 1] = key
    }
}

// Простой обмен (пузырьковая)
fun bubbleSort(a: DoubleArray, counter: Counter) {
    for (end in a.size - 1 downTo 1) {
        for (i in 0 until end) {
            counter.comparisons++
            if (a[i] > a[i + 1]) {
                swap(a, i, i + 1)
                counter.moves++
            }
        }
    }
}

// Сравнение, которое заодно считает себя в counter
fun less(x: Double, y: Double, counter: Counter): Boolean {
    counter.comparisons++
    return x < y
}

// Метод Шелла
fun shellSort(a: DoubleArray, counter: Counter) {
    var d = a.size / 2
    while (d > 0) {
        for (i in d until a.size) {
            val key = a[i]
            var j = i
            while (j >= d && less(key, a[j - d], counter)) {
                a[j] = a[j - d]
                counter.moves++
                j -= d
            }
            a[j] = key
        }
        d /= 2
    }
}

// Метод Хоара
fun quickSort(a: DoubleArray, counter: Counter, left: Int = 0, right: Int = a.size - 1) {
    val median = a[(left + right) / 2]
    var i = left
    var j = right
    while (i <= j) {
        while (less(a[i], median, counter)) i++
        while (less(median, a[j], counter)) j--
        if (i <= j) {
            swap(a, i, j)
            counter.moves++
            i++
            j--
        }
    }
    if (left < j) quickSort(a, counter, left, j)
    if (i < right) quickSort(a, counter, i, right)
}

// Метод пирамиды
fun heapSort(a: DoubleArray, counter: Counter) {
    for (i in a.size / 2 - 1 downTo 0) siftDown(a, i, a.size, counter)
    for (end in a.size - 1 downTo 1) {
        swap(a, 0, end)
        counter.moves++
        siftDown(a, 0, end, counter)
    }
}

// Пирамида: опускание элемента вниз, пока он не станет больше своих детей
fun siftDown(a: DoubleArray, start: Int, size: Int, counter: Counter) {
    var root = start
    while (true) {
        var largest = root
        val left = 2 * root + 1
        val right = left + 1
        if (left < size && less(a[largest], a[left], counter)) largest = left
        if (right < size && less(a[largest], a[right], counter)) largest = right
        if (largest == root) return
        swap(a, root, largest)
        counter.moves++
        root = largest
    }
}
