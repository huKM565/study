package ru.hukm

import kotlin.math.min

class SortStats(val comparisons: Long, val writes: Long)

private class Counter {
    var comparisons = 0L
    var writes = 0L
}

fun cycleSort(a: DoubleArray): SortStats {
    val counter = Counter()
    for (cycleStart in 0 until a.size - 1) {
        var item = a[cycleStart]
        var pos = findPosition(a, cycleStart, item, counter)
        if (pos == cycleStart) continue
        pos = skipEqual(a, pos, item, counter)
        var displaced = a[pos]
        a[pos] = item
        item = displaced
        counter.writes++
        while (pos != cycleStart) {
            pos = findPosition(a, cycleStart, item, counter)
            pos = skipEqual(a, pos, item, counter)
            displaced = a[pos]
            a[pos] = item
            item = displaced
            counter.writes++
        }
    }
    return SortStats(counter.comparisons, counter.writes)
}

private fun findPosition(a: DoubleArray, cycleStart: Int, item: Double, counter: Counter): Int {
    var pos = cycleStart
    for (i in cycleStart + 1 until a.size) {
        counter.comparisons++
        if (a[i] < item) pos++
    }
    return pos
}

private fun skipEqual(a: DoubleArray, pos: Int, item: Double, counter: Counter): Int {
    var p = pos
    while (true) {
        counter.comparisons++
        if (a[p] != item) return p
        p++
    }
}

fun cycleSortRanked(a: DoubleArray): SortStats {
    val counter = Counter()
    val order = a.indices.sortedWith { i, j ->
        counter.comparisons++
        a[i].compareTo(a[j])
    }
    val target = IntArray(a.size)
    order.forEachIndexed { rank, index -> target[index] = rank }
    placeCycles(a, target, counter)
    return SortStats(counter.comparisons, counter.writes)
}

fun cycleSortLogistic(a: DoubleArray, mean: Double, scale: Double): SortStats {
    val counter = Counter()
    val n = a.size
    val bucket = IntArray(n) { min((logisticCdf(a[it], mean, scale) * n).toInt(), n - 1) }
    val start = IntArray(n + 1)
    for (b in bucket) start[b + 1]++
    for (b in 0 until n) start[b + 1] += start[b]
    val members = IntArray(n)
    val free = start.copyOf(n)
    for (i in 0 until n) members[free[bucket[i]]++] = i
    val target = IntArray(n)
    for (b in 0 until n) {
        for (k in start[b] until start[b + 1]) {
            val i = members[k]
            var rank = start[b]
            for (m in start[b] until start[b + 1]) {
                val j = members[m]
                if (j == i) continue
                counter.comparisons++
                if (a[j] < a[i] || a[j] == a[i] && j < i) rank++
            }
            target[i] = rank
        }
    }
    placeCycles(a, target, counter)
    return SortStats(counter.comparisons, counter.writes)
}

private fun placeCycles(a: DoubleArray, target: IntArray, counter: Counter) {
    for (cycleStart in a.indices) {
        if (target[cycleStart] == cycleStart) continue
        var item = a[cycleStart]
        var pos = target[cycleStart]
        while (true) {
            val displaced = a[pos]
            val next = target[pos]
            a[pos] = item
            target[pos] = pos
            counter.writes++
            if (pos == cycleStart) break
            item = displaced
            pos = next
        }
    }
}
