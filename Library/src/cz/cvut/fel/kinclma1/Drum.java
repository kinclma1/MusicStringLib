package cz.cvut.fel.kinclma1;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 24.9.12
 * Time: 20:20
 * To change this template use File | Settings | File Templates.
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

    public static Drum fromString(String drum) {
        for (Drum d : Drum.values()) {
            if (d.toString().equals(drum)) {
                return d;
            }
        }
        return null;
    }

    public String toMusicString() {
        return String.format("[%1$s]", this.toString());
    }

    public static Drum fromInt(int value) {
        return Drum.values()[value - 35];
    }

    public int toInteger() {
        return this.ordinal() + 35;
    }
}
