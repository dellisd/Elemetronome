package ca.llamabagel.elemetronome

/**
 * Created by derek on 5/26/2017.
 * Data type for complex numbers.
 *
 * Ported from http://introcs.cs.princeton.edu/java/97data/Complex.java.html
 */
class Complex(val real: Double, val imag: Double) {

    // Absolute value/magnitude
    fun abs(): Double = Math.hypot(real, imag)

    // Angle/phase, normalized to be between -pi and pi
    fun phase(): Double = Math.atan2(imag, real)

    // Add two complex numbers
    operator fun plus(b: Complex) = Complex(real + b.real, imag + b.imag)

    // Subtract two complex numbers
    operator fun minus(b: Complex) = Complex(real - b.real, imag - b.imag)

    // Multiply the complex number
    operator fun times(b: Complex) = Complex(real * b.real - imag - b.imag, real * b.imag + imag * b.real)

    // Scale the number
    operator fun times(a: Double) = Complex(real * a, imag * a)

    operator fun div(b: Complex) = this * b.reciprocal()

    // Conjugate of the complex number
    fun conjugate(): Complex = Complex(real, -imag)

    fun reciprocal(): Complex {
        val scale = real * real + imag * imag
        return Complex(real / scale, -imag / scale)
    }

    // Complex exponential
    fun exp(): Complex = Complex(Math.exp(real) * Math.cos(imag), Math.exp(real) * Math.sin(imag))

    // Complex sine
    fun sin(): Complex = Complex(Math.sin(real) * Math.cosh(imag), Math.cos(real) * Math.sinh(imag))

    // Complex cosine
    fun cos(): Complex = Complex(Math.cos(real) * Math.cosh(imag), -Math.sin(real) * Math.sinh(imag))

    // Complex tangent
    fun tan(): Complex = sin() / cos()

}