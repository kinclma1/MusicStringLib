package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 24.1.13
 * Time: 4:11
 * To change this template use File | Settings | File Templates.
 */
public abstract class BeatElement {

    protected MusicStringDuration duration;

    public BeatElement(MusicStringDuration duration) {
        this.duration = duration;
    }

    protected int value() {
        return -1;
    }

    protected abstract String getTone();

    public TGNote toTGNote(TGFactory factory) {
        return null;
    }
}
