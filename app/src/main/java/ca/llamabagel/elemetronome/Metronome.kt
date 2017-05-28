package ca.llamabagel.elemetronome

/**
 * Created by isaac on 5/27/2017.
 */
abstract class Metronome(private var bpm: Double, private var beatsPerBar: Int, private var beatSoundFrequency: Double, private var downBeatSoundFrequency: Double, private val lengthOfTickInSamples: Int = 1000, private var play: Boolean = true, private var audioGenerator: AudioGenerator = AudioGenerator(8000)){
    private var silenceLengthInSamples: Int = (((60 / bpm) * 8000) - lengthOfTickInSamples).toInt()

    init {
        audioGenerator.createPlayer()
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
            onTick()
            audioGenerator.writeSound(sound)
        } while (play)
    }

    fun stop() {
        play = false
        audioGenerator.destroyAudioTrack()
    }

    abstract fun onTick()
}