package ca.llamabagel.elemetronome

/**
 * Created by derek on 5/24/2017.
 * Encapsulation of various tempo markings with ranges of tempos in STARTING_BPM
 */
class TempoMarking(val min: Int, val max: Int, val name: String) {
    companion object {
        val tempos = listOf<TempoMarking>(
                TempoMarking(1, 24, "Larghissimo"),
                TempoMarking(25, 50, "Grave"),
                TempoMarking(51, 55, "Largo"),
                TempoMarking(56, 60, "Larghetto"),
                TempoMarking(61, 70, "Adagio"),
                TempoMarking(71, 85, "Andante"),
                TempoMarking(86, 100, "Moderato"),
                TempoMarking(101, 115, "Allegretto"),
                TempoMarking(116, 140, "Allegro"),
                TempoMarking(141, 150, "Vivace"),
                TempoMarking(151, 170, "Presto"),
                TempoMarking(171, Int.MAX_VALUE, "Prestissimo")
        )

        fun fromBpm(bpm: Int) : TempoMarking = tempos.filter { it.bpmIsInRange(bpm) }.last()
    }

    fun bpmIsInRange(bpm: Int) : Boolean = bpm in min..max
}