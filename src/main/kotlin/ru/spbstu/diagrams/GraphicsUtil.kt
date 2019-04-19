package ru.spbstu.diagrams

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D
import kotlin.math.ceil

interface Drawable {
    fun getSize(): Dimension
    fun draw(g: Graphics2D)
}


inline fun Graphics2D.withOtherColor(c: Color, body: () -> Unit) {
    val tmp = color
    color = c
    body()
    color = tmp
}

fun Circle(x: Double, y: Double, radius: Double) = Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius)

operator fun Dimension.times(scale: Double) = Dimension(ceil(width * scale).toInt(), ceil(height * scale).toInt())
