package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 14.9.12
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
class MusicStringNote implements Comparable<MusicStringNote> {
    private MusicStringTone tone;
    private Drum drum;
    private MusicStringDuration duration;

    public MusicStringNote(TGNote note, boolean drumTrack) {
        int value = note.getVoice().getBeat().getMeasure().getTrack().getString(note.getString()).getValue()
                + note.getValue();
        if(!drumTrack) {
            tone = new MusicStringTone(value);
        } else {
            drum = Drum.fromInt(value);
        }
        duration = new MusicStringDuration(note.getVoice().getDuration());
    }

    public MusicStringNote(String note, boolean drumTrack) {
        String[] toneAndDuration = note.split("(?=[a-z])");
        if (!drumTrack) {
            tone = new MusicStringTone(toneAndDuration[0]);
        } else {
            drum = Drum.fromString(toneAndDuration[0].substring(1, toneAndDuration[0].length() - 1));
        }
        duration = new MusicStringDuration(toneAndDuration[1]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(tone == null ? drum.toMusicString() : tone.toString());
        sb.append(duration.toString());
        return sb.toString();
    }

    public void configureTGVoice(TGVoice voice) {
        voice.getDuration().setValue(duration.toInteger());
        voice.getDuration().setDotted(duration.isDotted());
    }

    public TGNote toTGNote(TGFactory factory) {
        TGNote note = factory.newNote();
        note.setValue(tone != null ? tone.toInteger() : drum.toInteger());
        return note;
    }

    public int getDurationDiv128() {
        return duration.toIntegerDiv128();
    }

    int value() {
        return tone != null ? tone.toInteger() : drum.toInteger();
    }

    @Override
    public int compareTo(MusicStringNote note) {
        return value() - note.value();
    }

    Duration shortestDuration() {
        return duration.shortest();
    }
}
