package ru.bnat.graphics.windows


import ru.bnat.graphics.convertation.CartesianScreenPlane
import ru.bnat.graphics.painters.FractalPainter
import ru.bnat.graphics.windows.components.MainPanel
import ru.bnat.math.fractals.Mandelbrot
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import java.lang.Exception
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.stream.FileImageOutputStream
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.sin

class Window : JFrame(){
    private val mainPanel: MainPanel
    private val controlPanel: JPanel
    private val btnExit: JButton
    private val cbColor: JCheckBox
    private val cbProp: JCheckBox
    private val btnSaveImage: JButton
    private val dim: Dimension

    private val painter: FractalPainter
    private val cs0: (Float) -> Color = {
        if (abs(it) < 1e-10) Color.BLACK else Color.WHITE
    }
    private val cs1: (Float) -> Color = {
        Color.getHSBColor(
            abs(cos(100 * it)),
            (log10(abs(sin(10 * it)))),
            abs(sin(100 * it))
        )
    }
    private val cs2: (Float) -> Color = {
        Color.getHSBColor(
            abs(cos(5 * it)),
            (log10(abs(sin(10 * it)))),
            abs(sin(10 * it)).toFloat()
        )
    }
    private val cs3: (Float) -> Color = { value ->
        if (value >= 1) Color.BLACK
        if (value < 0) Color.WHITE
        Color(
            Math.abs(Math.sin(Math.PI / 8 + 12 * value)).toFloat(),
            Math.abs(Math.cos(Math.PI / 6 - 12 * value)).toFloat(),
            Math.abs(Math.cos(Math.PI / 2 + 12 * value)).toFloat()
        )
    }

    init{
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        dim = Dimension(500, 500)
        minimumSize = dim

        val plane = CartesianScreenPlane(
            -1,
            -1,
            -2.0,
            1.0,
            -1.0,
            1.0
        )

        val m = Mandelbrot()
        painter = FractalPainter(plane, m)

        mainPanel = MainPanel(painter)
        controlPanel = JPanel()
        controlPanel.border =
            BorderFactory.createTitledBorder(
                "Управление отображением"
            )
        btnExit = JButton("Выход")
        btnExit.addActionListener {
            System.exit(0)
        }
        cbColor = JCheckBox("Цвет", false)
        cbProp = JCheckBox("Соблюдение пропорций",
            false)
        btnSaveImage=JButton("Сохранить")
        btnSaveImage.addActionListener{
            painter.rBuf?.let{
                val buf = BufferedImage(it.width, it.height+80, BufferedImage.TYPE_INT_RGB)
                val g=buf.graphics
                g.drawImage(it, 0, 0, it.width, it.height, null)
                g.color=Color.WHITE
                g.fillRect(0, it.height, it.width, it.height+80)
                g.color=Color.BLACK
                g.drawLine(0, 0, 0, it.height+80)
                g.drawLine(0, it.height+79, it.width, it.height+79)
                g.drawLine(0, it.height, it.width, it.height)
                g.drawLine(0, 0, it.width, 0)
                g.drawLine(it.width-1, 0, it.width-1, it.height+79)
                g.drawString("x min = "+painter.plane.xMin, 30, it.height+30)
                g.drawString("y min = "+painter.plane.yMin, 30, it.height+60)
                g.drawString("x max = "+painter.plane.xMax, it.width/2, it.height+30)
                g.drawString("y max = "+painter.plane.yMax, it.width/2, it.height+60)
                saveImageFile(buf, this)
            }
        }
        setColorScheme()
        cbColor.addActionListener {
            setColorScheme()
            mainPanel.repaint()
        }

        val gl = GroupLayout(contentPane)
        layout = gl
        gl.setVerticalGroup(
            gl.createSequentialGroup()
                .addGap(4)
                .addComponent(mainPanel,
                    (dim.height*0.7).toInt(),
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE)
                .addGap(4)
                .addComponent(controlPanel,
                    GroupLayout.PREFERRED_SIZE,
                    GroupLayout.PREFERRED_SIZE,
                    GroupLayout.PREFERRED_SIZE)
                .addGap(4)
        )
        gl.setHorizontalGroup(
            gl.createSequentialGroup()
                .addGap(4)
                .addGroup(
                    gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(mainPanel,
                            (dim.width*0.9).toInt(),
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.DEFAULT_SIZE
                        )
                        .addComponent(controlPanel)
                )
                .addGap(4)
        )

        val gl2 = GroupLayout(controlPanel)
        controlPanel.layout = gl2

        gl2.setVerticalGroup(
            gl2.createSequentialGroup()
                .addGap(4)
                .addGroup(
                    gl2.createParallelGroup(
                        GroupLayout.Alignment.CENTER
                    )
                        .addGroup(
                            gl2.createSequentialGroup()
                                .addComponent(cbColor,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.PREFERRED_SIZE)
                                .addGap(4)
                                .addComponent(cbProp,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.PREFERRED_SIZE)
                        )
                        .addComponent(
                            btnSaveImage,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE
                        )
                        .addComponent(btnExit,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE)
                )
                .addGap(4)
        )
        gl2.setHorizontalGroup(
            gl2.createSequentialGroup()
                .addGap(4)
                .addGroup(
                    gl2.createParallelGroup(
                        GroupLayout.Alignment.LEADING
                    )
                        .addComponent(cbColor)
                        .addComponent(cbProp)

                )
                .addGap(4, 4, Int.MAX_VALUE)
                .addComponent(btnSaveImage)
                .addGap(4)
                .addComponent(btnExit)
                .addGap(4)
        )

        pack()
        painter.plane.realWidth = mainPanel.width
        painter.plane.realHeight = mainPanel.height

        cbProp.addActionListener{
            painter.flagProp=cbProp.isSelected
            if (cbProp.isSelected) {
                painter.ymin=painter.plane.yMin
                painter.ymax=painter.plane.yMax
                painter.xmin=painter.plane.xMin
                painter.xmax=painter.plane.xMax
                val k = painter.plane.realWidth.toDouble() / painter.plane.realHeight.toDouble()
                val dy=(painter?.ymax ?: 0.0) - (painter?.ymin ?: 0.0)
                val dx=(painter?.xmax ?: 0.0)-(painter?.xmin ?: 0.0)
                if (dy>dx){
                    val d=k*dy-dx
                    painter.plane.xMin-=d/2.0
                    painter.plane.xMax+=d/2.0
                }
                else{
                    val d=1.0/k*dx-dy
                    painter.plane.yMin-=d/2.0
                    painter.plane.yMax+=d/2.0
                }
            }
            else {
                painter.plane.yMin=painter?.ymin ?: 0.0
                painter.plane.yMax=painter?.ymax ?: 0.0
                painter.plane.xMin=painter?.xmin ?: 0.0
                painter.plane.xMax=painter?.xmax ?: 0.0
            }
            painter.created=false
            mainPanel.repaint()
        }
        isVisible = true
    }
    private fun getFileName(fileFilter: FileNameExtensionFilter, parent: Component? = null): String? {
        var s: String? = null
        val d = JFileChooser()
        d.isAcceptAllFileFilterUsed = false
        d.fileFilter = fileFilter
        d.currentDirectory = File(".")
        d.dialogTitle = "Сохранить файл"
        d.approveButtonText = "Сохранить"
        val res: Int = d.showSaveDialog(parent)
        if (res == JFileChooser.APPROVE_OPTION) {
            s = d.selectedFile.absolutePath ?: ""
            if (!d.fileFilter.accept(d.selectedFile)) {
                s += "." + (fileFilter?.extensions?.get(0) ?: "")
            }
        }
        return s
    }
    private fun saveImageFile(img: BufferedImage, parent: Component? = null): Boolean {
        val filefilter = FileNameExtensionFilter("JPG File", "jpg")
        val fileName = getFileName(filefilter, parent)
        if (fileName != null) {
            val res = saveImage(fileName, img)
            return res
        }
        return true
    }

    private fun saveImage(fileName: String, img: BufferedImage): Boolean =
        saveImage(File(fileName), img)


    private fun saveImage(file: File, img: BufferedImage): Boolean {
        var ok = false
        if (!file.exists() || file.canWrite()) {
            var wr: FileImageOutputStream? = null
            try {
                wr = FileImageOutputStream(file)
                val iwr = ImageIO.getImageWritersByFormatName("JPG").next()
                val iwp = iwr.defaultWriteParam
                iwp.compressionMode = ImageWriteParam.MODE_EXPLICIT
                iwp.compressionQuality = 1F
                iwr.output = wr
                val iioi = IIOImage(img, null, null)
                iwr.write(null, iioi, iwp)
                ok = true
            } catch (ex: Exception) {
                println(ex)
            } finally {
                wr?.close()
                return ok
            }
        }
        return ok
    }
    private fun setColorScheme() {
        val cs = if (cbColor.isSelected) cs2 else cs0
        painter.setColorScheme(cs)
    }
}