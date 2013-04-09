package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.models.TGVoice;

/**
 * A rest - a beat element of silence
 */
class MusicStringRest extends BeatElement {

    /**
     * Creates a rest of a given duration
     * @param duration duration of the rest - should be shared among all beat
     */
    public MusicStringRest(MusicStringDuration duration) {
        super(duration);
    }

    /**
     * Returns the music string "tone" for a rest - a "R"
     * @return music string "tone" for a rest - a "R"
     */
    @Override
    protected String getTone() {
        return "R";
    }

    @Override
    public String toString() {
        return getTone() + duration.toString();
    }
}
