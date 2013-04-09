package cz.cvut.fel.kinclma1;

/**
 * Anything in a note - a tone, or a drum instrument
 */
public abstract class NoteContent {

    /**
     * Returns the MIDI value of this
     * @return MIDI value of this
     */
    public abstract int toInt();

    public abstract String toString();

    /**
     * Returns null, overriden in MusicStringTone
     * @return null
     */
    protected String relativeTone() {
        return null;
    }
}
