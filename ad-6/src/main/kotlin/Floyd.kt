package ru.hukm

const val INF = Int.MAX_VALUE / 4

class FloydResult(val size: Int, val distance: Array<IntArray>, val sequence: Array<IntArray>) {

    fun distance(from: Int, to: Int): Int? {
        val d = distance[from - 1][to - 1]
        return if (d >= INF) null else d
    }

    fun path(from: Int, to: Int): List<Int> {
        if (from == to) return listOf(from)
        if (distance[from - 1][to - 1] >= INF) return emptyList()
        val out = mutableListOf(from)
        append(from - 1, to - 1, out)
        return out
    }

    private fun append(from: Int, to: Int, out: MutableList<Int>) {
        val k = sequence[from][to]
        if (k == -1) {
            out.add(to + 1)
        } else {
            append(from, k, out)
            append(k, to, out)
        }
    }
}

fun floyd(size: Int, edges: List<Edge>): FloydResult {
    val d = Array(size) { IntArray(size) { INF } }
    val s = Array(size) { IntArray(size) { -1 } }
    for (i in 0 until size) d[i][i] = 0
    for (edge in edges) {
        val a = edge.from - 1
        val b = edge.to - 1
        if (edge.distance < d[a][b]) {
            d[a][b] = edge.distance
            d[b][a] = edge.distance
        }
    }
    for (k in 0 until size) {
        for (i in 0 until size) {
            if (i == k || d[i][k] >= INF) continue
            for (j in 0 until size) {
                if (j == k || i == j || d[k][j] >= INF) continue
                val through = d[i][k] + d[k][j]
                if (through < d[i][j]) {
                    d[i][j] = through
                    s[i][j] = k
                }
            }
        }
    }
    return FloydResult(size, d, s)
}
