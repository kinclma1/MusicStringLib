package cz.cvut.fel.kinclma1;

/**
 * Drum note / Drum instrument
 */
public class MusicStringDrum extends NoteContent {
    private Drum drum;

    /**
     * Creates drum from music string
     * @param drumString Music string drum representation
     */
    public MusicStringDrum(String drumString) {
        drum = Drum.fromString(drumString);
    }

    /**
     * Creates a drum from its MIDI number
     * @param drumInt MIDI number of the drum
     */
    public MusicStringDrum(int drumInt) {
        drum = Drum.fromInt(drumInt);
    }

    /**
     * Return the MIDI number of this
     * @return MIDI number of this
     */
    @Override
    public int toInt() {
        return drum.toInteger();
    }

    @Override
    public String toString() {
        return drum.toMusicString();
    }
}
