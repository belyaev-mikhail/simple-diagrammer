package ru.spbstu.diagrams.swing

import ru.spbstu.diagrams.Drawable
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JFrame
import javax.swing.JPanel

data class DrawablePanel(val drawable: Drawable): JPanel() {
    override fun getPreferredSize(): Dimension = drawable.getSize() as Dimension

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g as Graphics2D
        drawable.draw(g)
    }
}

fun displayDrawable(drawable: Drawable,
                    header: String = "Diagram",
                    closeOperation: Int = JFrame.EXIT_ON_CLOSE) {
    JFrame(header).apply {
        defaultCloseOperation = closeOperation
        setLocationRelativeTo(null)
        add(DrawablePanel(drawable))
        pack()
        isVisible = true
    }
}
