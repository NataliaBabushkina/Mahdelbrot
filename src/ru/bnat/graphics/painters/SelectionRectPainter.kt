package ru.bnat.graphics.painters


import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import kotlin.math.abs
import kotlin.math.min

class SelectionRectPainter {
    var g: Graphics? = null

    private var p1: Point? = null
    private var p2: Point? = null
    private var draw = false


    val leftTopPoint: Point
        get() {
            val r = createRect()
            return Point(r.x, r.y)
        }

    val rightBottomPoint: Point
        get() {
            val r = createRect()
            return Point(r.x + r.width, r.y + r.height)
        }

    fun start(p: Point) {
        draw = true
        p1 = p
        p2 = p
        paint()
    }

    fun start(x: Int, y: Int) {
        start(Point(x, y))
    }

    fun stop() {
        draw = false
        paint()
    }

    fun shift(p: Point) {
        paint()
        p2 = p
        paint()
    }

    private fun createRect(): Rectangle {
        val r = Rectangle()
        r.x = min(p1?.x ?: 0, p2?.x ?: 0)
        r.y = min(p1?.y ?: 0, p2?.y ?: 0)
        r.width = abs((p1?.x ?: 0) - (p2?.x ?: 0))
        r.height = abs((p1?.y ?: 0) - (p2?.y ?: 0))
        return r
    }

    private fun paint() {
        g?.setXORMode(Color.WHITE)
        g?.color = Color.BLACK
        val r = createRect()
        g?.drawRect(r.x, r.y, r.width, r.height)
    }

}