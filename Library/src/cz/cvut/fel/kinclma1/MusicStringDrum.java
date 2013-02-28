package cz.cvut.fel.kinclma1;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 24.1.13
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
 */
public class MusicStringDrum extends NoteContent {
    private Drum drum;

    public MusicStringDrum(String drumString) {
        drum = Drum.fromString(drumString);
    }

    public MusicStringDrum(int drumInt) {
        drum = Drum.fromInt(drumInt);
    }

    @Override
    public int toInt() {
        return drum.toInteger();
    }

    @Override
    public String toString() {
        return drum.toMusicString();
    }
}
