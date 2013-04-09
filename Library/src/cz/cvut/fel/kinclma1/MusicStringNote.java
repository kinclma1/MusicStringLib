package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGNote;

/**
 * A note - anything else in a beat than a rest - regular note, or a drum note/instrument
 */
class MusicStringNote extends BeatElement implements Comparable<MusicStringNote> {
    private NoteContent content;

    /**
     * Creates a MusicStringNote from a TGNote
     * @param note source TGNote
     * @param drumTrack drum track flag
     * @param duration duration of the note - common for the whole beat
     */
    public MusicStringNote(TGNote note, boolean drumTrack, MusicStringDuration duration) {
        super(duration);
        int value = note.getVoice().getBeat().getMeasure().getTrack().getString(note.getString()).getValue()
                + note.getValue();
        content = drumTrack ? new MusicStringDrum(value) : new MusicStringTone(value);
    }

    /**
     * Creates a MusicStringNote from a music string note representation
     * @param note source music string
     * @param drumTrack drum track flag
     * @param duration duration of the note - common for the whole beat
     */
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

    /**
     * Returns a TGNote equivalent of this note
     * @param factory TuxGuitar factory needed for creating TG objects
     * @return TGNote equivalent of this note
     */
    @Override
    public TGNote toTGNote(TGFactory factory) {
        TGNote note = factory.newNote();
        note.setValue(value());
        return note;
    }

    /**
     * Returns the MIDI value of this note's tone or drum instrument
     * @return MIDI value of this note's tone or drum instrument
     */
    @Override
    protected int value() {
        return content.toInt();
    }

    @Override
    public int compareTo(MusicStringNote note) {
        return value() - note.value();
    }

    /**
     * Returns a string representation of the tone, null for drums
     * @return String representation of the tone, null for drums
     */
    @Override
    protected String getTone() {
        return content.relativeTone();
    }
}
