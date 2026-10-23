package ru.hukm.ui

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hukm.Edge
import ru.hukm.NODE_COUNT

private val positions = mapOf(
    1 to Offset(0.06f, 0.56f),
    2 to Offset(0.22f, 0.15f),
    3 to Offset(0.19f, 0.89f),
    4 to Offset(0.30f, 0.56f),
    5 to Offset(0.54f, 0.56f),
    6 to Offset(0.62f, 0.15f),
    7 to Offset(0.63f, 0.89f),
    8 to Offset(0.76f, 0.56f),
    9 to Offset(0.94f, 0.56f),
)

@Composable
fun GraphView(edges: List<Edge>, path: List<Int>, modifier: Modifier = Modifier) {
    val measurer = rememberTextMeasurer()
    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val outline = MaterialTheme.colorScheme.outline
    val surface = MaterialTheme.colorScheme.surface
    val pathFill = MaterialTheme.colorScheme.primaryContainer
    val pathEdges = path.zipWithNext().toSet()

    Canvas(modifier) {
        val radius = 22.dp.toPx()
        fun point(node: Int): Offset {
            val p = positions.getValue(node)
            return Offset(radius + p.x * (size.width - 2 * radius), radius + p.y * (size.height - 2 * radius))
        }

        for (edge in edges) {
            val a = point(edge.from)
            val b = point(edge.to)
            val dir = (b - a) / (b - a).getDistance()
            val start = a + dir * radius
            val end = b - dir * radius
            val onPath = (edge.from to edge.to) in pathEdges
            val color = if (onPath) primary else outline
            val arrow = 12.dp.toPx()

            drawLine(
                color = color,
                start = start,
                end = end - dir * arrow,
                strokeWidth = if (onPath) 4.dp.toPx() else 2.dp.toPx(),
            )
            val normal = Offset(-dir.y, dir.x) * (arrow / 2)
            val base = end - dir * arrow
            val head = Path().apply {
                moveTo(end.x, end.y)
                lineTo(base.x + normal.x, base.y + normal.y)
                lineTo(base.x - normal.x, base.y - normal.y)
                close()
            }
            drawPath(head, color)

            val text = measurer.measure(
                edge.weight.toString(),
                TextStyle(
                    fontSize = 13.sp,
                    color = if (onPath) primary else onSurface,
                    fontWeight = if (onPath) FontWeight.Bold else FontWeight.Normal,
                ),
            )
            val pad = 4.dp.toPx()
            val mid = (a + b) / 2f
            val topLeft = Offset(mid.x - text.size.width / 2f, mid.y - text.size.height / 2f)
            val boxTopLeft = topLeft - Offset(pad, pad / 2)
            val boxSize = Size(text.size.width + 2 * pad, text.size.height + pad)
            drawRect(surface, boxTopLeft, boxSize)
            drawRect(color, boxTopLeft, boxSize, style = Stroke(1.dp.toPx()))
            drawText(text, topLeft = topLeft)
        }

        for (node in 1..NODE_COUNT) {
            val center = point(node)
            val inPath = node in path
            drawCircle(if (inPath) pathFill else surface, radius, center)
            drawCircle(if (inPath) primary else onSurface, radius, center, style = Stroke(if (inPath) 3.dp.toPx() else 1.5.dp.toPx()))
            val text = measurer.measure(node.toString(), TextStyle(fontSize = 16.sp, color = onSurface, fontWeight = FontWeight.SemiBold))
            drawText(text, topLeft = Offset(center.x - text.size.width / 2f, center.y - text.size.height / 2f))
        }
    }
}
