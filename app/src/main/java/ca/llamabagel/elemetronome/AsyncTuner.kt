package ca.llamabagel.elemetronome

import android.media.AudioRecord
import android.os.AsyncTask
import ca.llamabagel.elemetronome.TunerFragment.Companion.SAMPLE_RATE
import org.jtransforms.fft.DoubleFFT_1D

/**
 * Created by derek on 6/12/2017.
 */
class AsyncTuner(val audioRecord: AudioRecord?, val updateListener: OnUpdateListener) : AsyncTask<Unit, Pair<Double, Note.PitchData>, Unit>() {

    val minBufferSize = 8192

    var shouldRun = true

    interface OnUpdateListener {
        fun update(frequency: Double, pitchData: Note.PitchData?)
    }

    override fun onPreExecute() {
        super.onPreExecute()

        // Cancel this AsyncTask if the audioRecord that was passed in is unusable
        if (audioRecord == null || audioRecord.state == AudioRecord.STATE_UNINITIALIZED) {
            cancel(true)
        }
    }

    override fun doInBackground(vararg p0: Unit?) {
        while (shouldRun) {
            val buffer = ShortArray(minBufferSize)
            audioRecord?.read(buffer, 0, minBufferSize)
            val fft = DoubleFFT_1D(minBufferSize.toLong())
            var bufferD = DoubleArray(buffer.size) { buffer[it].toDouble() }
            bufferD = lowPassFrequency(bufferD)
            fft.realForward(bufferD)

            // Find the index of the maximum magnitude
            var max = Double.NEGATIVE_INFINITY
            var maxIndex = 0
            for (i in 1 until bufferD.size / 2) {
                // The real component
                val re = bufferD[2*i]
                // The imaginary component
                val im = bufferD[2*i+1]
                val mag = Math.sqrt(re*re +im*im)

                if (mag > max) {
                    max = mag
                    maxIndex = i
                }
            }

            val freq = maxIndex * SAMPLE_RATE.toDouble() / (bufferD.size)
            publishProgress(Pair(freq, Note.note(maxIndex * TunerFragment.SAMPLE_RATE.toDouble() / (bufferD.size))))
        }
    }

    override fun onProgressUpdate(vararg values: Pair<Double, Note.PitchData>?) {
        updateListener.update(values[0]?.first ?: 0.0, values[0]?.second)
    }

    private fun lowPassFrequency(input: DoubleArray): DoubleArray {
        val RC = 1.0 / (Note.C.frequency(7) * 2 *Math.PI)
        val dt = 1.0 / TunerFragment.SAMPLE_RATE.toDouble()
        val alpha = dt / (RC + dt)
        val output: DoubleArray = DoubleArray(input.size) {0.0}
        output[0] = input[0]

        for(i in 1 until output.size) {
            output[i] = output[i - 1] + (alpha*(input[i] - output[i - 1]))
        }

        return output
    }

}