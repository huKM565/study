package ru.hukm

class Edge(val from: Int, val to: Int, val weight: Int)

const val NODE_COUNT = 9
const val START_NODE = 1

val edges = listOf(
    Edge(1, 2, 20),
    Edge(1, 4, 15),
    Edge(1, 3, 25),
    Edge(2, 6, 32),
    Edge(2, 4, 18),
    Edge(3, 4, 26),
    Edge(3, 7, 53),
    Edge(4, 6, 19),
    Edge(4, 5, 37),
    Edge(4, 7, 15),
    Edge(5, 8, 22),
    Edge(5, 7, 24),
    Edge(6, 8, 31),
    Edge(6, 9, 45),
    Edge(7, 8, 17),
    Edge(7, 9, 52),
    Edge(8, 9, 26),
)
