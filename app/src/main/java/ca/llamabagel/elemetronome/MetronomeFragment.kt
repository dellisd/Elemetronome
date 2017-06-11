package ca.llamabagel.elemetronome

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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

    private var toneDurationInMillis = 15
    private var animationDurationInMillis: Long = 0

    private var metronomeIsSilenced = true

    // The defaul interval in ms (this is 120BPM)
    var interval: Long = 500

    // The BPM that the metronome should be at
    var BPM = 120

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_metronome, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        animationDurationInMillis = interval - 100

        // Create the animation that makes the screen pulsate
        val fadeOut = AnimationUtils.loadAnimation(activity!!, R.anim.fade_out)

        // Initialize the metronomeTimer
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

        // Start the timer in the background
        metronomeTimer?.start()

        tempoSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update interval
                interval = ((60.0f / (progress + 1)) * 1000).toLong()

                // Update animation duration
                animationDurationInMillis = interval - 100

                // If the metronome is currently going
                if (idk_youCanMakeAThICCC_t1ckIfUWant)
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

                // Update BPM
                BPM = progress + 1

                bpmText.text = getString(R.string.metronome_tempo, BPM)

                tempoName.text = TempoMarking.fromBpm(BPM).name
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Set the initial text being displayed on the screen
        bpmText.text = getString(R.string.metronome_tempo, (60 / (interval / 1000.0f)).toInt())
        tempoName.text = TempoMarking.fromBpm((60 / (interval / 1000.0f)).toInt()).name

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
            }
        }
        incrementButton.setOnClickListener { _ ->
            tempoSeekBar.progress++
        }
        decrementButton.setOnClickListener { _ ->
            tempoSeekBar.progress--
        }
    }
}