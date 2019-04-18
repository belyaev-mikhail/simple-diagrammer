package ru.spbstu.diagrams

data class Cell(val x: Int, val y: Int)
fun midpoint(lhv: Cell, rhv: Cell) = Cell((lhv.x + rhv.x) / 2, (lhv.y + rhv.y) / 2)
