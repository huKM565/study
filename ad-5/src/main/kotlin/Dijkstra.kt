package ru.hukm

class Label(val distance: Int, val from: Int?) {
    override fun toString() = "[$distance, ${from ?: "–"}]"
}

class DijkstraResult(val labels: Map<Int, Label>)

fun dijkstra(nodeCount: Int, edges: List<Edge>, start: Int): DijkstraResult {
    val labels = mutableMapOf(start to Label(0, null))
    val permanent = mutableSetOf<Int>()
    var current: Int? = start

    while (current != null) {
        permanent += current
        val distance = labels.getValue(current).distance
        for (edge in edges) {
            if (edge.from != current || edge.to in permanent) continue
            val candidate = distance + edge.weight
            val old = labels[edge.to]
            if (old == null || candidate < old.distance) labels[edge.to] = Label(candidate, current)
        }
        current = (1..nodeCount)
            .filter { it !in permanent && it in labels }
            .minByOrNull { labels.getValue(it).distance }
    }
    return DijkstraResult(labels)
}

fun pathTo(result: DijkstraResult, target: Int): List<Int> {
    if (target !in result.labels) return emptyList()
    val path = mutableListOf<Int>()
    var node: Int? = target
    while (node != null) {
        path.add(0, node)
        node = result.labels.getValue(node).from
    }
    return path
}
