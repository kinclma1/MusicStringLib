package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGNote;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 14.9.12
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
class MusicStringNote extends BeatElement implements Comparable<MusicStringNote> {
    private NoteContent content;

    public MusicStringNote(TGNote note, boolean drumTrack, MusicStringDuration duration) {
        super(duration);
        int value = note.getVoice().getBeat().getMeasure().getTrack().getString(note.getString()).getValue()
                + note.getValue();
        content = drumTrack ? new MusicStringDrum(value) : new MusicStringTone(value);
    }

    public MusicStringNote(String note, boolean drumTrack, MusicStringDuration duration) {
        super(duration);
        String[] toneAndDuration = note.split("(?=[a-z])");
        String tone = toneAndDuration[0];
        content = drumTrack ? new MusicStringDrum(tone.substring(1, tone.length() - 1)) : new MusicStringTone(tone);
    }

    @Override
    public String toString() {
        return content.toString() + duration.toString();
    }

    @Override
    public TGNote toTGNote(TGFactory factory) {
        TGNote note = factory.newNote();
        note.setValue(value());
        return note;
    }

    @Override
    protected int value() {
        return content.toInt();
    }

    @Override
    public int compareTo(MusicStringNote note) {
        return value() - note.value();
    }

    @Override
    protected String getTone() {
        return content.relativeTone();
    }
}
