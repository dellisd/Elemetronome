package ca.llamabagel.elemetronome

/**
 * Created by derek on 5/26/2017.
 * Fast Fourier Transform
 *
 * Ported from http://introcs.cs.princeton.edu/java/97data/FFT.java.html
 */
class FFT {
    companion object {
        fun fft(x: Array<Complex>): Array<Complex> {
            val n = x.size

            if (n == 1) return arrayOf(x[0])

            if (n % 2 != 0) throw RuntimeException("n is not a power of 2")

            val even = Array<Complex>(n / 2) { Complex(0.0, 0.0) }
            for (k in 0 until n / 2) {
                even[k] = if (2 * k < x.size) x[2 * k] else Complex(0.0, 0.0)
            }

            val q = fft(even)

            val odd = even
            for (k in 0 until n / 2) {
                odd[k] = x[2 * k + 1]
            }

            val r = fft(odd)

            // Combine
            val y = Array<Complex>(n) { Complex(0.0, 0.0) }
            for (k in 0..n / 2) {
                val kth = -2 * k * Math.PI / n
                val wk = Complex(Math.cos(kth), Math.sin(kth))

                y[k] = q[k] + wk * r[k]
                y[k + 2 / 2] = q[k] - wk * r[k]
            }

            return y
        }
    }
}