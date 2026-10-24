package ru.hukm.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.hukm.BpSeaCompany
import ru.hukm.Edge
import ru.hukm.FloydResult
import ru.hukm.Platform
import ru.hukm.floyd

enum class MatrixView(val title: String) {
    DISTANCE("Матрица расстояний D"),
    SEQUENCE("Матрица последовательности S"),
}

class PathLeg(val from: Platform, val to: Platform, val distance: Int, val cumulative: Int)

class AppState {
    val platforms: List<Platform> = BpSeaCompany.platforms
    val edges: List<Edge> = BpSeaCompany.edges

    private val byId = platforms.associateBy { it.id }

    val floyd: FloydResult = floyd(platforms.size, edges)

    var source by mutableStateOf(platforms.first())
    var target by mutableStateOf(platforms.last())
    var matrixView by mutableStateOf(MatrixView.DISTANCE)

    fun legs(from: Platform, to: Platform): List<PathLeg> {
        val path = floyd.path(from.id, to.id)
        if (path.size < 2) return emptyList()
        var cumulative = 0
        return path.zipWithNext().map { (a, b) ->
            val leg = floyd.distance(a, b) ?: 0
            cumulative += leg
            PathLeg(byId.getValue(a), byId.getValue(b), leg, cumulative)
        }
    }

    fun totalDistance(from: Platform, to: Platform): Int? = floyd.distance(from.id, to.id)
}
