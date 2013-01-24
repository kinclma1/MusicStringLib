package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.models.TGVoice;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 24.1.13
 * Time: 4:11
 * To change this template use File | Settings | File Templates.
 */
public class BeatElement {
    //todo BeatContents encapsulating a list of beat elements, later maybe stick duration in it and remove element
    protected MusicStringDuration duration;

    public void handleTGVoice(TGVoice voice) {
        voice.getDuration().setValue(duration.toInteger());
        voice.getDuration().setDotted(duration.isDotted());
        voice.setEmpty(false);
    }

    public int getDurationDiv128() {
        return duration.toIntegerDiv128();
    }

    MusicStringDuration shortestDuration() {
        return duration.shortest();
    }
}
