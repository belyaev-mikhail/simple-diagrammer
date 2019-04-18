package ru.spbstu.diagrams

import org.apache.commons.text.StringEscapeUtils
import java.awt.*
import java.awt.geom.*

private val horizontalLines = setOf('-', '=', '~', '>', '<', '+', '*')
private val verticalLines = setOf('|', ':', '!', 'v', 'V', '^', '+', '*')

private val lineSymbols = horizontalLines + verticalLines

private val boldSymbols = setOf('!', '=')
private val dottedSymbols = setOf(':', '~')

class Diagram(val props: DiagramProperties, val lines: List<Line>, val texts: List<Text>, val w: Int, val h: Int): Drawable {
    private val debug get() = props.debugGrid
    private val fontSpec get() = props.fontSpec

    companion object {
        private const val cellSize = 15
        private fun Line.drawMe(g: Graphics2D) {
            val startXAdjust = if(leftArrow) 0.5 * cellSize else 0.0
            val endXAdjust = if(rightArrow) -0.5 * cellSize else 0.0
            val startYAdjust = if(upArrow) 0.5 * cellSize else 0.0
            val endYAdjust = if(downArrow) -0.5 * cellSize else 0.0

            val startX = cellSize / 2.0 + start.x * cellSize
            val endX = cellSize / 2.0 + end.x * cellSize
            val startY = cellSize / 2.0 + start.y * cellSize
            val endY = cellSize / 2.0 + end.y * cellSize

            g.draw(Line2D.Double(
                    startX + startXAdjust,
                    startY + startYAdjust,
                    endX + endXAdjust,
                    endY + endYAdjust
            ))

            if(leftArrow) {
                g.fill(arrowTip(startX, startY, 0.0))
            }

            if(rightArrow) {
                g.fill(arrowTip(endX, endY, Math.PI))
            }

            if(upArrow) {
                g.fill(arrowTip(startX, startY, Math.PI / 2))
            }

            if(downArrow) {
                g.fill(arrowTip(endX, endY, -Math.PI / 2))
            }
        }

        private fun Cell.toPoint2d() : Point2D.Double =
                Point2D.Double(cellSize / 2.0 + x * cellSize, cellSize / 2.0 + y * cellSize)

        private fun arrowTip(x: Double, y: Double, rotation: Double): Shape {
            val tip = Path2D.Double()
            tip.moveTo(x, y)
            tip.lineTo(x + cellSize, y - cellSize / 3)
            tip.lineTo(x + cellSize, y + cellSize / 3)
            tip.closePath()

            val transform = AffineTransform.getRotateInstance(rotation, x, y)

            return transform.createTransformedShape(tip)
        }

        private val fontSpecs by lazy {
            val names = mutableMapOf<String, Font>()
            val families = mutableMapOf<String, Font>()

            val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
            for(font in ge.allFonts) {
                names[font.name.toLowerCase()] = font.deriveFont(cellSize.toFloat())
                families[font.family.toLowerCase()] = font.deriveFont(Font.PLAIN, cellSize.toFloat())
            }
            names to families
        }
        private val fontNames get() = fontSpecs.first
        private val fontFamilies get() = fontSpecs.second

        fun fromMatrix(matrix: CharMatrix, props: DiagramProperties = DiagramProperties()): Diagram {
            val lines = mutableListOf<Line>()
            val texts = mutableListOf<Text>()
            with(matrix) {
                while(true) {
                    var he = find {
                        !it.second.isWhitespace()
                                && it.second != '\u0000'
                                && it.second !in lineSymbols }?.first
                    val line = kotlin.collections.mutableListOf<Cell>()
                    while(he != null) {
                        if(line.isNotEmpty()
                                && matrix[line.last()].isWhitespace()
                                && matrix[he].isWhitespace()) break
                        line += he
                        he = he.right ?: break
                    }
                    val filtered = line.dropLastWhile { matrix[it].isWhitespace() }.dropWhile { matrix[it].isWhitespace() }
                    val word = filtered.map { matrix[it] }.joinToString("")
                    if(line.isNotEmpty()) {
                        for(c in line) matrix[c] = replacer(matrix[c], Direction.H)
                    } else break
                    texts += Text(StringEscapeUtils.unescapeHtml4(word),
                            midpoint(filtered.first(), filtered.last()))
                }

                while(true)  {
                    var he = find { it.second in horizontalLines }?.first
                    val line = kotlin.collections.mutableListOf<Cell>()
                    var bold = false
                    var dotted = false
                    var lArrow = false
                    var rArrow = false
                    while(he != null && matrix[he] in horizontalLines) {
                        line += he
                        if(matrix[he] in boldSymbols) bold = true
                        if(matrix[he] in dottedSymbols) dotted = true
                        if(matrix[he] == '<') lArrow = true
                        if(matrix[he] == '>') rArrow = true
                        he = he.right ?: break
                    }
                    if(line.isNotEmpty()) {
                        for(c in line) matrix[c] = replacer(matrix[c], Direction.H)
                    } else break
                    if(line.size > 1) lines.add(
                            Line(
                                    line.first(),
                                    line.last(),
                                    bold = bold,
                                    dotted = dotted,
                                    leftArrow = lArrow,
                                    rightArrow = rArrow
                            )
                    )
                }

                while(true)  {
                    var he = find { it.second in verticalLines }?.first
                    val line = kotlin.collections.mutableListOf<Cell>()
                    var bold = false
                    var dotted = false
                    var uArrow = false
                    var dArrow = false
                    while(he != null && matrix[he] in verticalLines) {
                        line += he
                        if(matrix[he] in boldSymbols) bold = true
                        if(matrix[he] in dottedSymbols) dotted = true
                        if(matrix[he] == '^') uArrow = true
                        if(matrix[he] in kotlin.collections.setOf('V', 'v')) dArrow = true
                        he = he.down ?: break
                    }
                    if(line.isNotEmpty()) {
                        for(c in line) matrix[c] = replacer(matrix[c], Direction.V)
                    } else break
                    if(line.size > 1) lines.add(
                            Line(
                                    line.first(),
                                    line.last(),
                                    bold = bold,
                                    dotted = dotted,
                                    upArrow = uArrow,
                                    downArrow = dArrow
                            )
                    )
                }

            }

            return Diagram(props, lines, texts, matrix.width, matrix.height)
        }
    }

    override fun getSize(): Dimension =
            Dimension(cellSize / 2 + cellSize * w, cellSize / 2 + cellSize * h)

    override fun draw(g: Graphics2D) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

        val font = fontSpec
                .toLowerCase()
                .split(";", ",")
                .asSequence()
                .mapNotNull { fontNames[it] ?: fontFamilies[it] }
                .firstOrNull()
                ?: g.font

        g.font = font.deriveFont((font.size2D * props.textScale).toFloat())
        g.background = Color(0, 0, 0, 0)

        if(debug) {
            g.withOtherColor(Color.LIGHT_GRAY) {
                for (i in 0..w)
                    g.drawLine(i * 15, 0, i * 15, h * 15)
                for (i in 0..h)
                    g.drawLine(0, i * 15, w * 15, i * 15)
            }
        }

        g.color = Color.BLACK

        for(line in lines) {
            val width = if(line.bold) 3.5f else 1.5f
            if(line.dotted) {
                g.stroke = BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, floatArrayOf(9.0f), 0.0f)
            } else {
                g.stroke = BasicStroke(width)
            }

            line.drawMe(g)
        }

        for(text in texts) {
            val fm = g.getFontMetrics(g.font)
            val center = text.center.toPoint2d()

            if(debug) {
                g.withOtherColor(Color.LIGHT_GRAY) {
                    g.fill(Circle(center.x, center.y, cellSize / 4.0))
                }
            }

            val x = center.x - fm.stringWidth(text.data) / 2
            val y = center.y - fm.height / 2 + fm.ascent

            g.drawString(text.data, x.toFloat(), y.toFloat())
        }
    }
}
