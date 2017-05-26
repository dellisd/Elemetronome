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

/**
 * Created by derek on 5/25/2017.
 * Fragment for all tuner-related stuff
 */
class TunerFragment() : Fragment() {
    private var audioRecord: AudioRecord? = null

    private val handler = Handler()

    val runnable: Thread = object: Thread() {
        val amp = getAmplitude()


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
                8000,
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
                // TODO: Start runnable
            }
        } else {
            getLiveAudioFeed()
            // TODO: Start runnable
        }
    }

    private fun getAmplitude(): Double {
        val minBufferSize = AudioRecord.getMinBufferSize(
                8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        )

        val buffer = ShortArray(minBufferSize)
        audioRecord?.read(buffer, 0, minBufferSize)

        var max = 0
        for (s in buffer) {
            if (Math.abs(s.toInt()) > max) max = Math.abs(s.toInt())
        }

        return max.toDouble()
    }
}