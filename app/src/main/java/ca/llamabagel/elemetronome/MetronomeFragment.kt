package ca.llamabagel.elemetronome

import android.app.ActivityManager
import android.app.Service
import android.content.Intent
import android.os.Bundle
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

    private var metronomeServiceTimer: MetronomeService? = null

    private var idk_youCanMakeAThICCC_t1ckIfUWant: Boolean = false

    // The defaul interval in ms (this is 120BPM)
    var interval: Long = 500

    // The BPM that the metronome should be at
    var BPM = 120

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_metronome, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        // Create the animation that makes the screen pulsate
        val fadeOut = AnimationUtils.loadAnimation(activity, R.anim.fade_out)

        // Make sure that the duration is set to something reasonable from the start
        fadeOut.duration = interval

        tempoSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                interval = ((60.0f / (progress + 1)) * 1000).toLong()

                BPM = progress + 1

                // Change the interval of the fadeOut animation
                fadeOut.duration = interval

                // Start the metronome
                changeMetronomeBPM((progress + 1).toDouble(), idk_youCanMakeAThICCC_t1ckIfUWant)

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
            /*if (!idk_youCanMakeAThICCC_t1ckIfUWant)
                backgroundImage.setAlpha(0.0f)
            else
                backgroundImage.setAlpha(0.4f)*/

            if (idk_youCanMakeAThICCC_t1ckIfUWant) {
                if (metronomeServiceTimer == null) {
                    metronomeServiceTimer = object: MetronomeService((60 / (interval / 1000.0)), 4, 523.25, 587.33) {
                        override fun onTick() {
                            // Make the screen pulsate
                            backgroundImage.startAnimation(fadeOut)
                        }
                    }
                }

                metronomeServiceTimer?.play()
                metronomeButton.text = getString(R.string.metronome_stop)
            } else {
                metronomeServiceTimer?.stop()

                metronomeButton.text = getString(R.string.metronome_start)
            }
        }
    }

    private fun stopMetronome() {
        val stopMetronomeIntent = Intent(activity, MetronomeService::class.java)
        stopMetronomeIntent.putExtra(MetronomeService.STOP_METRONOME, true)
        activity.startService(stopMetronomeIntent)
    }

    private fun changeMetronomeBPM(newBPM: Double, shouldStartAfterChange: Boolean){
        val changeMetronomeBPMIntent = Intent(activity, MetronomeService::class.java)
        changeMetronomeBPMIntent.putExtra(MetronomeService.NEW_BPM, newBPM)
        changeMetronomeBPMIntent.putExtra(MetronomeService.SHOULD_START_AFTER_BPM_CHANGE, shouldStartAfterChange)
        activity.startService(changeMetronomeBPMIntent)
    }
}