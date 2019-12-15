package ru.bnat.graphics.convertation

data class CartesianPlane(
    var xMin: Double,
    var xMax: Double,
    var yMin: Double,
    var yMax: Double
)

class CartesianScreenPlane(
    realWidth: Int,
    realHeight: Int,
    var xMin: Double,
    var xMax: Double,
    var yMin: Double,
    var yMax: Double
) {

    private val listeners = mutableListOf<(Int, Int) -> Unit>()

    val width: Int
        get() = realWidth - 1
    val height: Int
        get() = realHeight - 1
    val xDen: Double
        get() = width.toDouble() / (xMax - xMin)
    val yDen: Double
        get() = height.toDouble() / (yMax - yMin)
    var realWidth: Int = -1
        get() = field
        set(value) {
            val old = field
            field = value
            for (l in listeners) {
                l.invoke(old, field)
            }
        }

    var realHeight: Int = -1
        get() = field
        set(value) {
            val old = field
            field = value
            for (l in listeners) {
                l.invoke(old, field)
            }
        }

    init {
        this.realWidth = realWidth
        this.realHeight = realHeight
    }

    fun addResizeListener(f: (Int, Int) -> Unit) {
        listeners.add(f)
    }
}