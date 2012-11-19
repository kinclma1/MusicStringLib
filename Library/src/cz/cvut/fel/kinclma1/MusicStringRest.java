package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.models.TGVoice;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 15.9.12
 * Time: 3:07
 * To change this template use File | Settings | File Templates.
 */
class MusicStringRest {

    private MusicStringDuration duration;

    public MusicStringRest(TGVoice tgVoice) {
        duration = new MusicStringDuration(tgVoice.getDuration());
//        System.out.println(toString());
    }

    public MusicStringRest(String strRest) {
        duration = new MusicStringDuration(strRest.substring(1));
    }

    public TGVoice toTGVoice(TGVoice voice) {
        voice.getDuration().setValue(duration.toInteger());
        voice.getDuration().setDotted(duration.isDotted());
        voice.setEmpty(false);
        return voice;
    }

    @Override
    public String toString() {
        return "R" + duration.toString();
    }

    public int getDurationDiv128() {
        return duration.toIntegerDiv128();
    }
}
