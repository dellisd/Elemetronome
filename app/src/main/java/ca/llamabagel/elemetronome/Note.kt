package ca.llamabagel.elemetronome

/**
 * Created by derek on 5/27/2017.
 * An enum class containing useful information about note names and frequencies
 * TODO: Probably shouldn't be an enum. Should have static copies of notes that can be reused. (vars don't work so well in enums)
 *
 * @param value A numerical representation of the note for doing math on notes
 * @param letter The letter name of the note. e.g. C, A#
 * @param simple Whether the note name associated is "simple". Like, who the hell uses B#?
 * @param octave The octave in which the note belongs. Default is 4 (like A4).
 */
class Note(var value: Int, val letter: String, val simple: Boolean, val type: Type, var octave: Int = 4) {
    companion object {
        val B_SHARP = Note(0, "B#", false, Type.SHARP)
        val C = Note(0, "C", true, Type.NATURAL)
        val C_SHARP = Note(1, "C#", true, Type.SHARP)
        val D_FLAT = Note(1, "D♭", false, Type.FLAT)
        val D = Note(2, "D", true, Type.NATURAL)
        val D_SHARP = Note(3, "D#", true, Type.SHARP)
        val E_FLAT = Note(3, "E♭", true, Type.FLAT)
        val E = Note(4, "E", true, Type.NATURAL)
        val F_FLAT = Note(4, "F♭", false, Type.FLAT)
        val E_SHARP = Note(5, "E#", false, Type.FLAT)
        val F = Note(5, "F", true, Type.NATURAL)
        val F_SHARP = Note(6, "F#", true, Type.SHARP)
        val G_FLAT = Note(6, "G♭", true, Type.FLAT)
        val G = Note(7, "G", true, Type.NATURAL)
        val G_SHARP = Note(8, "G#", true, Type.SHARP)
        val A_FLAT = Note(8, "A♭", true, Type.FLAT)
        val A = Note(9, "A", true, Type.NATURAL)
        val A_SHARP = Note(10, "A#", true, Type.SHARP)
        val B_FLAT = Note(10, "B♭", true, Type.FLAT)
        val B = Note(11, "B", true, Type.NATURAL)
        val C_FLAT = Note(11, "C♭", false, Type.FLAT)

        var notes = arrayOf(B_SHARP, C, C_SHARP, D_FLAT, D, D_SHARP, E_FLAT, E, F_FLAT, E_SHARP,
                F, F_SHARP, G_FLAT, G, G_SHARP, A_FLAT, A, A_SHARP, B_FLAT, B, C_FLAT)

        /**
         * Standard tuning frequency. Can be adjusted for other standards (e.g. 442 Hz)
         */
        var A4 = 440.0

        /**
         * The twelfth root of two. Required in converting letter name to frequency
         */
        val a = 1.059463094359

        /**
         * Calculate the note name and octave, as well as some info about tuning from a
         * given frequency.
         *
         * @param frequency The frequency to obtain PitchData for
         */
        fun note(frequency: Double): PitchData {
            val semitones: Int = Math.round(Math.log(frequency / A4) / Math.log(a)).toInt()
            // Need to add 12 and then take mod again in case result after first mod is negative
            val noteValue : Int = (((A.value + semitones) % 12) + 12) % 12
            var octave: Int = 4+Math.floor(semitones.toDouble()/12.0).toInt()
            // Necessary because the calculation above will only increment octave for each time noteValue reaches the next A
            if (noteValue < A.value) {
                octave++
            }

            val notes = Note.notes.filter { it.value == noteValue && it.simple }

            // Sets each note's octave
            notes.forEach { it.octave = octave }

            //val tunefulnessHz = frequency - notes.last().frequency()
            val tunefulnessCents = 1200 * Math.log(frequency/notes.first().frequency())/Math.log(2.0)
            return PitchData(notes, tunefulnessCents)
        }
    }

    /**
     * Calculates the frequency of a note using the formula
     * f = f0 * a^n
     * where f0 is the frequency of A4, and n is the number of half steps between the note and A4
     *
     * Taken from http://www.phy.mtu.edu/~suits/NoteFreqCalcs.html
     *
     * @param octave The octave at which to use this note at.
     */
    fun frequency(octave: Int): Double {
        val semitones = (octave-4)*12 + (value-A.value)

        return A4 * Math.pow(a, semitones.toDouble())
    }

    /**
     * Calculates the frequency of a note using the formula and the note's given octave
     * f = f0 * a^n
     * where f0 is the frequency of A4, and n is the number of half steps between the note and A4
     *
     * Taken from http://www.phy.mtu.edu/~suits/NoteFreqCalcs.html
     */
    fun frequency(): Double = frequency(octave)


    /**
     * Class holding some information about the different types of notes, like naturals, sharps, flats
     *
     * @param value A numerical representation of the type of note
     * @param symbol The symbol used for the type of note
     */
    enum class Type(val value: Int, val symbol: String) {
        NATURAL(0, "♮"),
        SHARP(1, "#"),
        FLAT(2, "♭")
    }

    /**
     * Data class for encapsulating pitch data.
     * @param note List of notes belonging to the pitch. Each pitch has different note names, etc.
     * @param tunefulness How far off the pitch is from the correct in-tune note. In cents.
     */
    data class PitchData(val note: List<Note>, val tunefulness: Double)
}

