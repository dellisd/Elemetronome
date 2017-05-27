package ca.llamabagel.elemetronome;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void note() throws Exception {
        assertEquals(Note.D, Note.Companion.note(73.42).getNote().get(0));
        assertEquals(Note.A, Note.Companion.note(440.0).getNote().get(0));
        assertEquals(Note.G, Note.Companion.note(3135.96).getNote().get(0));
    }
}