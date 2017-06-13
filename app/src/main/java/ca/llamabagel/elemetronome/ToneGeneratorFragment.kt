package ca.llamabagel.elemetronome

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import kotlinx.android.synthetic.main.fragment_tone_generator.*
import kotlin.experimental.and

class ToneGeneratorFragment : Fragment() {
    companion object {
        fun newInstance(text: String): ToneGeneratorFragment {
            val m = ToneGeneratorFragment()
            val b = Bundle()
            b.putString("msg", text)
            m.arguments = b

            return m
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_tone_generator, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        fun playTone(note: Note) {
            val duration: Int = 3 // seconds
            val sampleRate: Int = 8000
            val numSamples: Int = duration * sampleRate
            val sample: DoubleArray = DoubleArray(numSamples)
            val freqOfTone: Double = note.frequency() // hz

            val generatedSnd: ByteArray = ByteArray(2 * numSamples)

            fun genTone() {
                // fill out the array
                for (i in 0 until numSamples) {
                    sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone))
                }

                // convert to 16 bit pcm sound array
                // assumes the sample buffer is normalised.
                var idx: Int = 0
                for (dVal: Double in sample) {
                    // scale to maximum amplitude
                    val value: Int = (dVal * 32767).toInt()
                    // in 16 bit wav PCM, first byte is the low order byte
                    generatedSnd[idx++] = (value and 0x00ff).toByte()
                    generatedSnd[idx++] = (value and 0xff00).shr(8).toByte()

                }
            }


            fun playSound() {
                val audioTrack: AudioTrack = AudioTrack(AudioManager.STREAM_MUSIC,
                        sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, numSamples,
                        AudioTrack.MODE_STATIC)
                audioTrack.write(generatedSnd, 0, generatedSnd.size)
                audioTrack.play()
            }

            genTone()
            playSound()
        }
        playSwitch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isOn: Boolean) {
                if (isOn) {
                    val notes = Note.notes.filter {it.letter == noteSpinner.selectedItem.toString()}
                    var note = notes.first()
                    note.octave = octaveSpinner.selectedItem.toString().toInt()
                    playTone(note)
                } else {
                }
            }
        })
    }
}
