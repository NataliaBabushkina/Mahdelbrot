package ru.bnat.graphics.windows.components


import ru.bnat.graphics.convertation.Converter
import ru.bnat.graphics.painters.FractalPainter
import ru.bnat.graphics.painters.SelectionRectPainter
import java.awt.Graphics
import java.awt.event.*
import javax.swing.JPanel
import javax.swing.SwingWorker


class MainPanel (var painter: FractalPainter): JPanel(){
    val srp = SelectionRectPainter()

    inner class BackgroundProcess : SwingWorker<Unit, Unit>() {
        override fun doInBackground() {
            painter.create()
        }

        override fun done() {
            painter.paint(this@MainPanel.graphics)
        }
    }

    private var bgProcess = BackgroundProcess()

    init{
        addComponentListener(
            object: ComponentAdapter(){
                override fun componentResized(e: ComponentEvent?) {
                    if (painter.xmin!=null && painter.xmax!=null && painter.ymin!=null && painter.ymax!=null) {
                        if (painter.flagProp) {
                            val k = painter.plane.realWidth.toDouble() / painter.plane.realHeight.toDouble()
                            val dy = (painter?.ymax ?: 0.0) - (painter?.ymin ?:0.0)
                            val dx = (painter?.xmax ?: 0.0) - (painter?.xmin ?: 0.0)
                            if (dy > dx) {
                                val d = k * dy - dx
                                painter.plane.xMin -= d / 2.0
                                painter.plane.xMax += d / 2.0
                            } else {
                                val d = 1.0 / k * dx - dy
                                painter.plane.yMin -= d / 2.0
                                painter.plane.yMax += d / 2.0
                            }
                        } else {
                            painter.plane.yMin = painter?.ymin ?: 0.0
                            painter.plane.yMax = painter?.ymax ?: 0.0
                            painter.plane.xMin = painter?.xmin ?: 0.0
                            painter.plane.xMax = painter?.xmax ?: 0.0
                        }
                        painter.created = false
                        repaint()
                    }
                }
            })
        addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent?) {
                super.mouseReleased(e)
                if (e != null && e.button == MouseEvent.BUTTON1) {
                    srp.stop()
                    val x_1=srp.leftTopPoint.x
                    val y_1=srp.leftTopPoint.y
                    val x_2=srp.rightBottomPoint.x
                    val y_2=srp.rightBottomPoint.y
                    if (!(x_1==x_2 && y_1==y_2)) {
                        val x1 = Converter.xScr2Crt(x_1, painter.plane)
                        val y1 = Converter.yScr2Crt(y_1, painter.plane)
                        val x2 = Converter.xScr2Crt(x_2, painter.plane)
                        val y2 = Converter.yScr2Crt(y_2, painter.plane)
                        painter.ymin = y2
                        painter.ymax = y1
                        painter.xmin = x1
                        painter.xmax = x2
                        if (painter.flagProp) {
                            val k = painter.plane.realWidth.toDouble() / painter.plane.realHeight.toDouble()
                            val dy = y1 - y2
                            val dx = x2 - x1
                            if (dy > dx) {
                                val d = k * dy - dx
                                painter.plane.xMin = x1 - d / 2.0
                                painter.plane.xMax = x2 + d / 2.0
                                painter.plane.yMax = y1
                                painter.plane.yMin = y2
                            } else {
                                val d = 1.0 / k * dx - dy
                                painter.plane.yMin = y2 - d / 2.0
                                painter.plane.yMax = y1 + d / 2.0
                                painter.plane.xMin = x1
                                painter.plane.xMax = x2
                            }
                        } else {
                            painter.plane.xMin = x1
                            painter.plane.xMax = x2
                            painter.plane.yMax = y1
                            painter.plane.yMin = y2
                        }
                    }
                    painter.created = false
                    repaint()
                }
            }

            override fun mousePressed(e: MouseEvent?) {
                super.mousePressed(e)
                if (e != null && e.button == MouseEvent.BUTTON1) {
                    srp.start(e.point)
                    srp.g = graphics
                }
            }
        })
        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent?) {
                super.mouseDragged(e)
                if (e != null) {
                    srp.shift(e.point)
                }
            }
        })
    }

    override fun paint(g: Graphics?) {
        painter.plane.realWidth = width
        painter.plane.realHeight = height
        super.paint(g)
        g?.let { painter.paint(it) }
        if (!painter.created) {
            if (!bgProcess.isDone) bgProcess.cancel(true)
            bgProcess = BackgroundProcess()
            bgProcess.execute()
        }
    }
}