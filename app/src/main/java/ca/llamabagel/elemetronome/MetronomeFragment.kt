package ca.llamabagel.elemetronome

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.media.ToneGenerator.TONE_DTMF_0
import android.media.ToneGenerator.TONE_DTMF_1
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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

        // Initialize the metronomeTimer
        metronomeTimer = object: AccurateTimer(SystemClock.uptimeMillis(), interval) {
            override fun onTick() {
                if (!metronomeIsSilenced) {
                    val toneToPlay = if (beatsView.selectedList[beatsView.currentBeat - 1]){
                        TONE_DTMF_0
                    } else {
                        TONE_DTMF_1
                    }
                    toneGenerator.startTone(toneToPlay, toneDurationInMillis)

                    beatsView.onTick(animationDurationInMillis)
                }
            }

            override fun onFinish() {}
        }

        // Start the timer in the background
        metronomeTimer?.start()

        fun startNewMetronome() {
            metronomeTimer?.cancel()

            metronomeTimer = object: AccurateTimer(SystemClock.uptimeMillis(), interval) {
                override fun onTick() {
                    if (!metronomeIsSilenced) {
                        val toneToPlay = if (beatsView.selectedList[beatsView.currentBeat - 1]){
                            TONE_DTMF_0
                        } else {
                            TONE_DTMF_1
                        }
                        toneGenerator.startTone(toneToPlay, toneDurationInMillis)

                        beatsView.onTick(animationDurationInMillis)
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
                beatsView.resetBeat()

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
                // Make sure that the animation and the tone can't be heard
                metronomeIsSilenced = true

                metronomeButton.text = getString(R.string.metronome_start)
            }
            else {
                // Make sure metronome can be heard
                metronomeIsSilenced = false

                metronomeButton.text = getString(R.string.metronome_stop)
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

                beatsView.resetBeat()

                // Update interval
                interval = ((60f / (BPM).toFloat()) * 1000f).toLong()

                // Update animation duration
                animationDurationInMillis = interval - 100

                // If the metronome is currently going
                if (idk_youCanMakeAThICCC_t1ckIfUWant) {
                    startNewMetronome()
                }
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

                beatsView.resetBeat()

                // Update interval
                interval = ((60f / (BPM).toFloat()) * 1000f).toLong()

                // Update animation duration
                animationDurationInMillis = interval - 100

                // If the metronome is currently going
                if (idk_youCanMakeAThICCC_t1ckIfUWant) {
                    startNewMetronome()
                }
            }

            false
        }

        // Update the display of number of beats
        numBeatsView.setText(beatsView.numBeats.toString())

        // Set the onClick listener for the beat increment and decrement buttons
        beatIncrementButton.setOnClickListener { _ ->
            beatsView.resetBeat()

            // Add another imageView
            beatsView.numBeats = beatsView.numBeats + 1

            numBeatsView.setText(beatsView.numBeats.toString())
        }
        beatDecrementButton.setOnClickListener { _ ->
            beatsView.resetBeat()

            if (beatsView.numBeats > 1) {
                // Remove an imageView
                beatsView.numBeats = beatsView.numBeats - 1
            }

            numBeatsView.setText(beatsView.numBeats.toString())
        }

    }
}