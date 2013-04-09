package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * Base class for MusicStringNote and MusicStringRest to eliminate duplicate code and allow polymorphism
 */
public abstract class BeatElement {

    protected MusicStringDuration duration;

    /**
     * Constructor setting duration
     * @param duration Duration of the element
     */
    public BeatElement(MusicStringDuration duration) {
        this.duration = duration;
    }

    protected int value() {
        return -1;
    }

    protected abstract String getTone();

    /**
     * Returns TGNote equivalent of a note, null for rest
     * @param factory TuxGuitar factory needed for creating TG objects
     * @return TGNote equivalent of a note, null for rest
     */
    public TGNote toTGNote(TGFactory factory) {
        return null;
    }
}
