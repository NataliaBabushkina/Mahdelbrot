package ru.bnat.math.fractals

import ru.bnat.math.Complex

class Mandelbrot() {
    var maxIter = 500
    val R = 2.0
    fun isInSet(x: Double, y: Double): Float {
        val c = Complex(x, y)
        var z = Complex()
        val R2 = R*R
        for (i in 1..maxIter){
            z = z * z + c
            if (z.arg2 > R2){
                return i.toFloat() / maxIter
            }
        }
        return 0.0F
    }
}
