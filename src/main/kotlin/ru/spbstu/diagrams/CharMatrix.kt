package ru.spbstu.diagrams

import java.io.BufferedReader
import java.io.InputStream

private fun <T> MutableList<T>.growWith(value: T, toSize: Int) { while(size < toSize) add(value) }

data class CharMatrix (val data: List<MutableList<Char>>): Iterable<Pair<Cell, Char>> {
    companion object {
        fun read(br: BufferedReader): CharMatrix = CharMatrix(
                br.lineSequence().mapTo(mutableListOf()) { it.toMutableList() } as List<MutableList<Char>>
        )
        fun read(`is`: InputStream): CharMatrix = read(`is`.bufferedReader())
    }

    val height get() = data.size
    val width = data.asSequence().map { it.size }.max() ?: 0

    init {
        for(line in data) line.growWith(' ', width)
    }

    inner class TheIterator(var x: Int = 0, var y: Int = 0) : Iterator<Pair<Cell, Char>> {
        override fun hasNext(): Boolean = y < height

        override fun next(): Pair<Cell, Char> = (Cell(x, y) to get(x, y)).also {
            ++x
            if(x >= width) { x = 0; ++y }
        }
    }
    override fun iterator(): Iterator<Pair<Cell, Char>> = TheIterator()

    operator fun get(x: Int, y: Int) = data[y][x]
    operator fun set(x: Int, y: Int, value: Char) { data[y][x] = value }

    operator fun get(cell: Cell) = get(cell.x, cell.y)
    operator fun set(cell: Cell, value: Char) { set(cell.x, cell.y, value) }

    override fun toString(): String = data.joinToString("\n") { it.joinToString("") }

    val Cell.up get() = if(y > 0) copy(y = y - 1) else null
    val Cell.down get() = if(y + 1 < height) copy(y = y + 1) else null
    val Cell.left get() = if(x > 0) copy(x = x - 1) else null
    val Cell.right get() = if(x + 1 < width) copy(x = x + 1) else null
}
