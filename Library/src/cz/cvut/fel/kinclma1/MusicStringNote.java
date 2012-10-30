package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 14.9.12
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
public class MusicStringNote {
    private Tone tone = null;
    private Drum drum = null;
    private Duration duration;
    private boolean dotted;

    public MusicStringNote(TGNote note, boolean drumTrack) {
        int value = note.getVoice().getBeat().getMeasure().getTrack().getString(note.getString()).getValue()
                + note.getValue();
        if(!drumTrack) {
            tone = new Tone(value);
        } else {
            drum = Drum.fromInt(value);
        }
        duration = Duration.fromInt(note.getVoice().getDuration().getValue());
        dotted = note.getVoice().getDuration().isDotted();
    }

    public MusicStringNote(String note, boolean drumTrack) {
        String[] toneAndDuration = note.split("(?=[a-z])");
        if (!drumTrack) {
            tone = new Tone(toneAndDuration[0]);
        } else {
            drum = Drum.fromString(toneAndDuration[0].substring(1, toneAndDuration[0].length() - 1));
        }
        duration = Duration.fromChar(toneAndDuration[1].charAt(0));
        dotted = toneAndDuration[1].charAt(toneAndDuration[1].length() - 1) == '.';
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tone == null ? drum.toMusicString() : tone.toString());
        sb.append(duration.toString());
        if(dotted) sb.append(".");
        return sb.toString();
    }

    public void configureTGVoice(TGVoice voice) {
        voice.getDuration().setValue(duration.toInteger());
        voice.getDuration().setDotted(dotted);
    }

    public TGNote toTGNote(TGFactory factory) {
        TGNote note = factory.newNote();
        note.setValue(tone != null ? tone.toInteger() : drum.toInteger());
        return note;
    }
}
