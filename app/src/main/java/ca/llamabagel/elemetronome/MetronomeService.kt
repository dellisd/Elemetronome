package ca.llamabagel.elemetronome

import android.app.IntentService
import android.content.Intent

/**
 * Created by isaac on 5/27/2017.
 */
class MetronomeService(private var bpm: Double, private var beatsPerBar: Int, private var beatSoundFrequency: Double = 523.25, private var downBeatSoundFrequency: Double = 587.33, private val lengthOfTickInSamples: Int = 1000, private var play: Boolean = true, private var audioGenerator: AudioGenerator = AudioGenerator(8000)) : IntentService("MetronomeService") {
    private var silenceLengthInSamples: Int = (((60 / bpm) * 8000) - lengthOfTickInSamples).toInt()
    companion object {
        const val STOP_METRONOME = "stop_metronome"
        const val NEW_BPM = "new_bpm"
        const val SHOULD_START_AFTER_BPM_CHANGE = "should_start"
    }

    override fun onHandleIntent(intent: Intent?) {
        // It could be an intent to stop the metronome from running
        // (make sure that the metronome is actually running)
        if (intent!!.hasExtra(STOP_METRONOME) && intent.getBooleanExtra(STOP_METRONOME, true) && play){
            stop()
        }

        // It could be an intent to change the bpm
        if (intent.hasExtra(NEW_BPM) && intent.hasExtra(SHOULD_START_AFTER_BPM_CHANGE)){
            if (play) {
                // Stop the metronome
                stop()
            }

            // Change the STARTING_BPM
            bpm = intent.getDoubleExtra(NEW_BPM, 120.toDouble())

            // If it should start up after being changed, then start it again
            if (intent.getBooleanExtra(SHOULD_START_AFTER_BPM_CHANGE, true)) {
                // Start it back up again
                play()
            }
        }
    }

    fun play() {
        val tick: DoubleArray = audioGenerator.getSineWave(this.lengthOfTickInSamples, 8000, beatSoundFrequency)
        val tock: DoubleArray = audioGenerator.getSineWave(this.lengthOfTickInSamples, 8000, downBeatSoundFrequency)
        val silence: Double = 0.toDouble()
        val sound: DoubleArray = DoubleArray(8000)
        var t: Int = 0
        var s: Int = 0
        var b: Int = 0
        var i: Int = 0
        do {
            while (i < sound.size && play){
                if (t < this.lengthOfTickInSamples) {
                    if (b == 0)
                        sound[i] = tock[t]
                    else
                        sound[i] = tick[t]
                    t++
                } else {
                    sound[i] = silence
                    s++
                    if (s >= this.silenceLengthInSamples) {
                        t = 0
                        s = 0
                        b++
                        if(b > (this.beatsPerBar - 1))
                            b = 0
                    }
                }
                i++
            }
            // Send a broadcast that the metronome has ticked

            audioGenerator.writeSound(sound)
        } while (play)
    }

    fun stop() {
        play = false
        audioGenerator.destroyAudioTrack()
    }
}