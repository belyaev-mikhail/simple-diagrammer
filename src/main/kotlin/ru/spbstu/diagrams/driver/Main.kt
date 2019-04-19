package ru.spbstu.diagrams.driver

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.double
import ru.spbstu.diagrams.CharMatrix
import ru.spbstu.diagrams.Diagram
import ru.spbstu.diagrams.DiagramProperties
import ru.spbstu.diagrams.export.exportAsPNG
import ru.spbstu.diagrams.export.exportAsSVG
import ru.spbstu.diagrams.swing.displayDrawable
import java.io.File

class Driver : CliktCommand() {
    val input by argument().optional()

    val output by option(help = "File to write the result to").default("-")
    val format by option(help = "Output format").choice("png", "svg", "gui")
    val debugGrid by option(help = "Show debug grid").flag()
    val font by option(help = "Set diagram font").default("")
    val textScale by option(help = "Modify text size by a factor").double().default(1.0)
    val scale by option(help = "Scale the whole diagram by a factor").double().default(1.0)

    override fun run() {

        val inputStream = when(input) {
            "-", null -> System.`in`
            else -> File(input).inputStream()
        }
        val matrix = CharMatrix.read(inputStream)
        val diag = Diagram.fromMatrix(
                matrix,
                DiagramProperties(
                        fontSpec = font,
                        debugGrid = debugGrid,
                        textScale = textScale,
                        diagramScale = scale
                )
        )

        val outputStream = when(output) {
            "-" -> System.out
            else -> File(output).outputStream()
        }
        val fmt = format ?: File(output).extension
        when(fmt) {
            "svg" -> exportAsSVG(diag, outputStream)
            "png" -> exportAsPNG(diag, outputStream)
            "gui" -> displayDrawable(diag)
            else -> throw IllegalArgumentException("Unknown output format: $fmt")
        }
    }
}

fun main(args: Array<String>) = Driver().main(args)
