package ru.spbstu.diagrams.export

import org.jfree.graphics2d.svg.SVGGraphics2D
import ru.spbstu.diagrams.Drawable
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.util.*
import javax.imageio.ImageIO

fun exportAsPNG(drawable: Drawable, file: File) {
    val bi = BufferedImage(drawable.getSize().width, drawable.getSize().height, BufferedImage.TYPE_INT_ARGB)
    drawable.draw(bi.createGraphics())
    ImageIO.write(bi, "png", file)
}

fun exportAsSVG(drawable: Drawable, file: File) {
    val g = SVGGraphics2D(drawable.getSize().width, drawable.getSize().height)
    drawable.draw(g)
    file.writeText(g.svgDocument)
}

fun exportAsPNG(drawable: Drawable, stream: OutputStream) {
    val bi = BufferedImage(drawable.getSize().width, drawable.getSize().height, BufferedImage.TYPE_INT_ARGB)
    drawable.draw(bi.createGraphics())
    ImageIO.write(bi, "png", stream)
}

fun exportAsSVG(drawable: Drawable, stream: OutputStream) {
    val g = SVGGraphics2D(drawable.getSize().width, drawable.getSize().height)
    drawable.draw(g)
    stream.bufferedWriter().use { it.write(g.svgDocument) }
}

fun exportAsSVGString(drawable: Drawable): String {
    val g = SVGGraphics2D(drawable.getSize().width, drawable.getSize().height)
    drawable.draw(g)
    return g.svgDocument
}

fun exportAsInlinePNG(drawable: Drawable): String {
    val bb = ByteArrayOutputStream()
    exportAsPNG(drawable, bb)
    val rawData = bb.toByteArray()
    val encoder = Base64.getEncoder()
    return "data:image/png;base64,${encoder.encodeToString(rawData)}"
}

fun exportAsInlineSVG(drawable: Drawable): String {
    val bb = ByteArrayOutputStream()
    exportAsSVG(drawable, bb)
    val rawData = bb.toByteArray()
    val encoder = Base64.getEncoder()
    return "data:image/svg;base64,${encoder.encodeToString(rawData)}"
}
