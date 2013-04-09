package cz.cvut.fel.kinclma1;

import java.util.regex.Pattern;

/**
 * A tone with its octave number
 */
public class MusicStringTone extends NoteContent {

    /**
     * Tones of the chromatic scale
     */
    enum RelativeTone {
        C,
        C_SHARP,
        D,
        D_SHARP,
        E,
        F,
        F_SHARP,
        G,
        G_SHARP,
        A,
        A_SHARP,
        B;

        @Override
        public String toString() {
            switch (this) {
                case C:
                    return "C";
                case C_SHARP:
                    return "C#";
                case D:
                    return "D";
                case D_SHARP:
                    return "D#";
                case E:
                    return "E";
                case F:
                    return "F";
                case F_SHARP:
                    return "F#";
                case G:
                    return "G";
                case G_SHARP:
                    return "G#";
                case A:
                    return "A";
                case A_SHARP:
                    return "A#";
                case B:
                    return "B";
                default:
                    return null;
            }
        }

        /**
         * Creates a tone from the given MIDI value
         * @param tone MIDI value of the tone
         * @return tone created from the given MIDI value
         */
        public static RelativeTone fromInt(int tone) {
            return RelativeTone.values()[tone];
        }

        /**
         * Returns the MIDI value of this tone
         * @return MIDI value of this tone
         */
        public int toInt() {
            return ordinal();
        }
    }

    private RelativeTone tone;
    private int octave;

    /**
     * Creates a MusicStringTone from the MIDI value
     * @param value MIDI tone number
     */
    public MusicStringTone(int value) {
        tone = RelativeTone.fromInt(value % 12);
        octave = value / 12;
    }

    /**
     * Parses a MusicStringTone from a music string
     * @param mStrTone a music string representation of the tone
     */
    public MusicStringTone(String mStrTone) {
        Pattern pattern = Pattern.compile("(^[A-G]#?1?[0-9][a-z]?\\.?)");
        if (!pattern.matcher(mStrTone).matches()) {
            throw new UnsupportedOperationException("Cannot create a MusicStringTone from string " + mStrTone);
        }
        int len;
        if (mStrTone.length() > 1 && mStrTone.charAt(1) == '#') {
            len = 2;
            for (RelativeTone t : RelativeTone.values()) {
                if (t.toString().equals(mStrTone.substring(0,2))) {
                    tone = t;
                    break;
                }
            }
        } else {
            len = 1;
            for (RelativeTone t : RelativeTone.values()) {
                if (t.toString().equals(mStrTone.substring(0,1))) {
                    tone = t;
                    break;
                }
            }
        }
        try {
            String[] newMStrTone = mStrTone.split("([a-z])");
            octave = Integer.parseInt(newMStrTone[0].substring(len,newMStrTone[0].length()));
        } catch (NumberFormatException ex) {
            octave = 5;
        }

    }

    @Override
    public String toString() {
        return tone.toString() + octave;
    }

    /**
     * Returns the MIDI value of the tone
     * @return MIDI value of the tone
     */
    @Override
    public int toInt() {
        return octave * 12 + tone.toInt();
    }

    /**
     * Returns the tone name without octave
     * @return tone name without octave
     */
    @Override
    protected String relativeTone() {
        return tone.toString();
    }
}
