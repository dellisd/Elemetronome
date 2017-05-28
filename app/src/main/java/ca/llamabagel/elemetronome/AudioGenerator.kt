package ca.llamabagel.elemetronome

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.experimental.and

/**
 * Created by isaac on 5/27/2017.
 */

class AudioGenerator(private var sampleRate: Int) {
    private var audioTrack: AudioTrack = AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, sampleRate, AudioTrack.MODE_STREAM)

    infix inline fun Byte.shl(shift: Int): Byte = (this.toInt() shl shift).toByte()
    infix inline fun Byte.shr(shift: Int): Byte = (this.toInt() shr shift).toByte()

    infix inline fun Short.shl(shift: Int): Short = (this.toInt() shl shift).toShort()
    infix inline fun Short.shr(shift: Int): Short = (this.toInt() shr shift).toShort()

    fun getSineWave(samples: Int, sampleRate: Int, frequencyOfTone: Double): DoubleArray {
        val sample: DoubleArray = DoubleArray(samples)
        for (i in 0..(samples - 1)) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / frequencyOfTone))
        }
        return sample
    }

    fun get16BitPcm(samples: DoubleArray): ByteArray {
        val generatedSound: ByteArray = ByteArray(2 * samples.size)
        var index: Int = 0
        for (sample in samples){
            val maxSample: Short = (sample * Short.MAX_VALUE).toShort()

            generatedSound[index++] = (maxSample and 0x00ff).toByte()
            generatedSound[index++] = ((maxSample and 0xff00.toShort()) shr 8).toByte()
        }

        return generatedSound
    }

    fun createPlayer() {
        audioTrack.play()
    }

    fun writeSound(samples: DoubleArray) {
        val generatedSound: ByteArray = get16BitPcm(samples)
        audioTrack.write(generatedSound, 0, generatedSound.size)
    }

    fun destroyAudioTrack() {
        audioTrack.stop()
        audioTrack.release()
    }



}