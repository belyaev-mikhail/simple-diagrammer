package ru.spbstu.diagrams

sealed class DrawableObject

data class Line(val start: Cell,
                val end: Cell,
                val bold: Boolean = false,
                val dotted: Boolean = false,
                val leftArrow: Boolean = false,
                val rightArrow: Boolean = false,
                val upArrow: Boolean = false,
                val downArrow: Boolean = false) : DrawableObject()

data class Text(val data: String, val center: Cell) : DrawableObject()

enum class Direction { H, V; }

fun replacer(ch: Char, direction: Direction) = when(ch) {
    '+', '*' -> when(direction) {
        Direction.H -> '|'
        Direction.V -> '-'
    }
    else -> '\u0000'
}

