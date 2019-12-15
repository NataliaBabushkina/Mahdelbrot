package ru.bnat.math


import kotlin.math.sqrt

class Complex(var x: Double, var y: Double) {
    constructor(z: Pair<Double, Double>) : this(z.first, z.second)
    constructor(): this(0.0, 0.0)

    operator fun plus(z: Complex) =
        Complex(x + z.x, y + z.y)

    operator fun minus(z: Complex) =
        Complex(x - z.x, y - z.y)

    operator fun times(z: Complex) =
        Complex(
            x * z.x - y * z.y,
            y * z.x + x * z.y
        )

    operator fun div(z: Complex): Complex{
        val d = z.x * z.x - z.y * z.y
        return Complex(
            (x * z.x + y * z.y) / d,
            (y * z.x - x * z.y) / d
        )
    }

    val arg: Double
        get(){
            return sqrt(x * x + y * y)
        }

    val arg2: Double
        get(){
            return x * x + y * y
        }
}