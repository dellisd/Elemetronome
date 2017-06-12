package ca.llamabagel.elemetronome

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_metronome.*
import java.util.*

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

    private var toneDurationInMillis = 15
    private var animationDurationInMillis: Long = 0

    private var metronomeIsSilenced = true

    private var currentBeat = 1
    private var totalBeats = 4

    private var beatTimer = Timer()

    // The default interval in ms (this is 120BPM)
    var interval: Long = 500

    // The BPM that the metronome should be at
    var BPM = 120

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_metronome, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        animationDurationInMillis = interval - 100

        // Create the animation that makes the screen pulsate
        val fadeOut = AnimationUtils.loadAnimation(activity as Context?, R.anim.fade_out)

        fun startNewMetronome() {
            metronomeTimer?.cancel()

            metronomeTimer = object: AccurateTimer(SystemClock.uptimeMillis(), interval) {
                override fun onTick() {
                    if (!metronomeIsSilenced) {
                        toneGenerator.startTone(ToneGenerator.TONE_DTMF_0, toneDurationInMillis)

                        // Make sure that the duration is set to something reasonable from the start
                        fadeOut.duration = animationDurationInMillis

                        // Make the screen pulsate
                        backgroundImage.startAnimation(fadeOut)
                    }
                }

                override fun onFinish() {}
            }

            // Restart the timer

            metronomeTimer?.start()
        }

        tempoSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update BPM
                BPM = progress + 1

                bpmText.text = getString(R.string.metronome_tempo, BPM)

                tempoName.text = TempoMarking.fromBpm(BPM).name
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Update interval
                interval = ((60f / (BPM).toFloat()) * 1000f).toLong()

                // Update animation duration
                animationDurationInMillis = interval - 100

                // If the metronome is currently going
                if (idk_youCanMakeAThICCC_t1ckIfUWant) {
                    startNewMetronome()
                }
            }
        })

        // Set the initial text being displayed on the screen
        bpmText.text = getString(R.string.metronome_tempo, (60f / (interval.toFloat() / 1000f)).toInt())
        tempoName.text = TempoMarking.fromBpm((60f / (interval.toFloat() / 1000f)).toInt()).name

        metronomeButton.setOnClickListener { _ ->
            idk_youCanMakeAThICCC_t1ckIfUWant = !idk_youCanMakeAThICCC_t1ckIfUWant
            if (!idk_youCanMakeAThICCC_t1ckIfUWant) {
                backgroundImage.setAlpha(0.0f)

                // Make sure that the animation and the tone can't be heard
                metronomeIsSilenced = true

                metronomeButton.text = getString(R.string.metronome_start)
            }
            else {
                backgroundImage.setAlpha(0.4f)

                // Make sure metronome can be heard
                metronomeIsSilenced = false

                metronomeButton.text = getString(R.string.metronome_stop)

                startNewMetronome()
            }
        }

        incrementButton.setOnClickListener { _ ->
            tempoSeekBar.progress++
        }
        decrementButton.setOnClickListener { _ ->
            tempoSeekBar.progress--
        }

        // Increments the tempo when the increment button is held down
        incrementButton.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                beatTimer = Timer()
                beatTimer.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        tempoSeekBar.progress++
                    }
                }, 500, 50)
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                beatTimer.cancel()
            }

            false
        }

        // Decrements the tempo when the decrement button is held down
        decrementButton.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                beatTimer = Timer()
                beatTimer.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        tempoSeekBar.progress--
                    }
                }, 500, 50)
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                beatTimer.cancel()
            }

            false
        }
    }
}