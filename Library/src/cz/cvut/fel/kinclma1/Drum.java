package cz.cvut.fel.kinclma1;

/**
 * Enumeration of MIDI percussion instruments
 */
public enum Drum {
    ACOUSTIC_BASS_DRUM, BASS_DRUM, SIDE_STICK, ACOUSTIC_SNARE,
    HAND_CLAP, ELECTRIC_SNARE, LOW_FLOOR_TOM, CLOSED_HI_HAT,
    HIGH_FLOOR_TOM, PEDAL_HI_HAT, LOW_TOM, OPEN_HI_HAT, LOW_MID_TOM,
    HI_MID_TOM, CRASH_CYMBAL_1, HIGH_TOM, RIDE_CYMBAL_1, CHINESE_CYMBAL,
    RIDE_BELL, TAMBOURINE, SPLASH_CYMBAL, COWBELL, CRASH_CYMBAL_2,
    VIBRASLAP, RIDE_CYMBAL_2, HI_BONGO, LOW_BONGO, MUTE_HI_CONGA,
    OPEN_HI_CONGA, LOW_CONGA, HIGH_TIMBALE, LOW_TIMBALE, HIGH_AGOGO,
    LOW_AGOGO, CABASA, MARACAS, SHORT_WHISTLE, LONG_WHISTLE,
    SHORT_GUIRO, LONG_GUIRO, CLAVES, HI_WOOD_BLOCK, LOW_WOOD_BLOCK,
    MUTE_CUICA, OPEN_CUICA, MUTE_TRIANGLE, OPEN_TRIANGLE;

    /**
     * Returns an enum item with the given name
     * @param drum Name of the percussion instrument
     * @return Enum item with the given name
     */
    public static Drum fromString(String drum) {
        for (Drum d : Drum.values()) {
            if (d.toString().equals(drum)) {
                return d;
            }
        }
        return null;
    }

    /**
     * Returns a music string representation of the instrument
     * @return Music string representation of the instrument
     */
    public String toMusicString() {
        return String.format("[%1$s]", toString());
    }

    /**
     * Returns an enum item with the given MIDI number
     * @param value MIDI number of the instrument
     * @return Enum item with the given MIDI number
     */
    public static Drum fromInt(int value) {
        return Drum.values()[value - 35];
    }

    /**
     * Returns the MIDI number of the instrument
     * @return MIDI number of the instrument
     */
    public int toInteger() {
        return ordinal() + 35;
    }
}
