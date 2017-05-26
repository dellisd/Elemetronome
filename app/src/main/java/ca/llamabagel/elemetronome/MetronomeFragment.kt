package ca.llamabagel.elemetronome

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_metronome.*

/**
 * Created by derek on 5/24/2017.
 * The fragment that contains everything metronome-related
 */
class MetronomeFragment : Fragment() {
    companion object {
        fun newInstance(text: String): MetronomeFragment {
            val m = MetronomeFragment()
            val b = Bundle()
            b.putString("msg", text)
            m.arguments = b

            return m
        }
    }

    private val toneGenerator: ToneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    private var metronomeTimer: AccurateTimer? = null

    private var idk_youCanMakeAThICCC_t1ckIfUWant: Boolean = false

    // The defaul interval in ms (this is 120BPM)
    var interval = 500

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_metronome, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        tempoSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                interval = ((60.0f / (progress + 1)) * 1000).toInt()

                metronomeTimer?.cancel()
                metronomeTimer = object: AccurateTimer(Long.MAX_VALUE / 2, interval.toLong()) {
                    override fun onTick() {
                        toneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 5)
                    }

                    // Doesn't need to be implemented as the interval until finish is extremely long
                    override fun onFinish() {}
                }

                bpmText.text = getString(R.string.metronome_tempo, progress + 1)

                tempoName.text = TempoMarking.fromBpm(progress + 1).name
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Set the initial text being displayed on the screen
        bpmText.text = getString(R.string.metronome_tempo, (60 / (interval / 1000.0f)).toInt())
        tempoName.text = TempoMarking.fromBpm((60 / (interval / 1000.0f)).toInt()).name

        metronomeButton.setOnClickListener { _ ->
            idk_youCanMakeAThICCC_t1ckIfUWant = !idk_youCanMakeAThICCC_t1ckIfUWant

            if (idk_youCanMakeAThICCC_t1ckIfUWant) {
                if (metronomeTimer == null) {
                    metronomeTimer = object: AccurateTimer(Long.MAX_VALUE / 2, interval.toLong()) {
                        override fun onTick() {
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 5)
                        }

                        // Doesn't need to be implemented as the interval until finish is extremely long
                        override fun onFinish() {}
                    }
                }

                metronomeTimer?.start()
                metronomeButton.text = getString(R.string.metronome_stop)
            } else {
                metronomeTimer?.cancel()

                metronomeButton.text = getString(R.string.metronome_start)
            }
        }
    }
}