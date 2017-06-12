package ca.llamabagel.elemetronome

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
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
class TunerFragment() : Fragment(), AsyncTuner.OnUpdateListener {

    private var audioRecord: AudioRecord? = null

    private var asyncTuner: AsyncTuner? = null

    val minBufferSize = 8192


    companion object {
        fun newInstance(text: String): TunerFragment {
            val m = TunerFragment()
            val b = Bundle()
            b.putString("msg", text)
            m.arguments = b

            return m
        }

        const val SAMPLE_RATE = 16000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_tuner, container, false)
    }

    override fun update(frequency: Double, pitchData: Note.PitchData?) {
        noteName?.text = pitchData?.note?.first()?.letter
        this@TunerFragment.frequency?.text = context.getString(R.string.tuner_frequency, frequency)
        centsIndicator?.x = (pitchData?.tunefulness?.times((tunerActivity?.width?.toFloat()?.div(100f) as Float))?.plus((tunerActivity?.width?.toFloat()?.div(2f) as Float))?.minus(10f))?.toFloat() as Float
        noteOctave?.text = pitchData.note.first().octave.toString()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
            audioRecord?.stop()
            audioRecord?.release()
        }

        // Clear tuner stuff
        asyncTuner?.shouldRun = false
        asyncTuner = null
    }

    override fun onPause() {
        super.onPause()

        if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
            audioRecord?.stop()
        }

        // Clear tuner stuff
        asyncTuner?.shouldRun = false
        asyncTuner = null
    }

    override fun onResume() {
        super.onResume()
        startListening()
    }

    private fun getLiveAudioFeed() {
        audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize
        )

        if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
            audioRecord?.startRecording()
        }
    }

    private fun startListening() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                getLiveAudioFeed()

                // Start tuning
                asyncTuner = AsyncTuner(audioRecord, this)
                asyncTuner?.execute()

            }
        } else {
            getLiveAudioFeed()
            asyncTuner = AsyncTuner(audioRecord, this)
            asyncTuner?.execute()
        }
    }
}