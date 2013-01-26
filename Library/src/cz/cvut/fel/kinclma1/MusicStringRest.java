package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.models.TGVoice;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 15.9.12
 * Time: 3:07
 * To change this template use File | Settings | File Templates.
 */
class MusicStringRest extends BeatElement {

    public MusicStringRest(MusicStringDuration duration) {
        super(duration);
    }

    @Override
    protected String getTone() {
        return "R";
    }

    @Override
    public String toString() {
        return getTone() + duration.toString();
    }
}
