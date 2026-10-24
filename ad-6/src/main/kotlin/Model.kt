package ru.hukm

class Platform(val id: Int, val name: String) {
    override fun toString() = "$id. $name"
}

class Edge(val from: Int, val to: Int, val distance: Int)

object BpSeaCompany {
    val platforms = listOf(
        Platform(1, "Платформа Alpha"),
        Platform(2, "Платформа Bravo"),
        Platform(3, "Платформа Charlie"),
        Platform(4, "Платформа Delta"),
        Platform(5, "Платформа Echo"),
        Platform(6, "Платформа Foxtrot"),
        Platform(7, "Платформа Golf"),
        Platform(8, "Платформа Hotel"),
        Platform(9, "Платформа India"),
    )

    val edges = listOf(
        Edge(1, 2, 5),
        Edge(1, 4, 20),
        Edge(1, 5, 4),
        Edge(1, 8, 6),
        Edge(1, 9, 15),
        Edge(2, 3, 6),
        Edge(3, 4, 15),
        Edge(3, 5, 10),
        Edge(4, 5, 2),
        Edge(4, 6, 7),
        Edge(5, 6, 3),
        Edge(5, 7, 12),
        Edge(5, 8, 13),
        Edge(5, 9, 2),
        Edge(7, 8, 5),
        Edge(8, 9, 5),
    )
}
