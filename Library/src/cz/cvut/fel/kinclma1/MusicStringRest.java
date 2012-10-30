package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.models.TGVoice;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 15.9.12
 * Time: 3:07
 * To change this template use File | Settings | File Templates.
 */
public class MusicStringRest {

    private Duration duration;
    private boolean dotted;

    public MusicStringRest(TGVoice tgVoice) {
        duration = Duration.fromInt(tgVoice.getDuration().getValue());
        dotted = tgVoice.getDuration().isDotted();
    }

    public MusicStringRest(String strRest) {
        duration = Duration.fromChar(strRest.charAt(1));
        dotted = strRest.charAt(strRest.length() - 1) == '.';
    }

    public TGVoice toTGVoice(TGVoice voice) {
        voice.getDuration().setValue(duration.toInteger());
        voice.getDuration().setDotted(dotted);
        return voice;
    }

    @Override
    public String toString() {
        return "R" + duration.toString() + (dotted ? "." : "");
    }
}
