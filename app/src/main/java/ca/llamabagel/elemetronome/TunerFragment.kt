package ca.llamabagel.elemetronome

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tuner.*
import org.jtransforms.fft.DoubleFFT_1D

/**
 * Created by derek on 5/25/2017.
 * Fragment for all tuner-related stuff
 */
class TunerFragment() : Fragment() {
    private var audioRecord: AudioRecord? = null

    private val handler = Handler()

    /**
     * Buffer size
     */
    private val minBufferSize = AudioRecord.getMinBufferSize(
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
    )

    val runnable: Thread = object: Thread() {
        override fun run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
            val buffer = ShortArray(minBufferSize)
            audioRecord?.read(buffer, 0, minBufferSize)

            val fft = DoubleFFT_1D(minBufferSize.toLong())
            val bufferD = DoubleArray(buffer.size) { buffer[it].toDouble() }
            fft.realForward(bufferD)

            // Find the index of the maximum magnitude
            var max = Double.NEGATIVE_INFINITY
            var maxIndex = -1
            for (i in 0 until bufferD.size / 2) {
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

            activity.runOnUiThread {
                noteName?.text = Note.note(maxIndex * 44100.0 / bufferD.size).note.first().letter
                amplitude?.text = Note.note(maxIndex * 44100.0 / (bufferD.size / 2)).toString()
            }

            handler.postDelayed(this, 100)
        }
    }

    companion object {
        fun newInstance(text: String): TunerFragment {
            val m = TunerFragment()
            val b = Bundle()
            b.putString("msg", text)
            m.arguments = b

            return m
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startListening()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_tuner, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioRecord?.stop()
        audioRecord?.release()
    }

    override fun onPause() {
        super.onPause()
        audioRecord?.stop()
    }

    override fun onResume() {
        super.onResume()
        startListening()
    }

    private fun getLiveAudioFeed() {
        val minBufferSize = AudioRecord.getMinBufferSize(
                8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize
        )

        audioRecord?.startRecording()
    }

    private fun startListening() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                getLiveAudioFeed()
                if (runnable.state == Thread.State.NEW) runnable.start()

            }
        } else {
            getLiveAudioFeed()
            if (runnable.state == Thread.State.NEW) runnable.start()
        }
    }

    private fun getAmplitude(): Double {
        val buffer = ShortArray(minBufferSize)
        audioRecord?.read(buffer, 0, minBufferSize)

        var max = 0
        buffer
                .asSequence()
                .filter { Math.abs(it.toInt()) > max }
                .forEach { max = Math.abs(it.toInt()) }

        return max.toDouble()
    }
}