package cz.cvut.fel.kinclma1;

/**
 * Enum of the MIDI instruments
 */
public enum Instrument {
    PIANO, BRIGHT_ACOUSTIC, ELECTRIC_GRAND, HONKEY_TONK,
    ELECTRIC_PIANO, ELECTRIC_PIANO_2, HARPISCHORD, CLAVINET, CELESTA,
    GLOCKENSPIEL, MUSIC_BOX, VIBRAPHONE, MARIMBA, XYLOPHONE,
    TUBULAR_BELLS, DULCIMER, DRAWBAR_ORGAN, PERCUSSIVE_ORGAN,
    ROCK_ORGAN, CHURCH_ORGAN, REED_ORGAN, ACCORDIAN, HARMONICA,
    TANGO_ACCORDIAN, GUITAR, STEEL_STRING_GUITAR, ELECTRIC_JAZZ_GUITAR,
    ELECTRIC_CLEAN_GUITAR, ELECTRIC_MUTED_GUITAR, OVERDRIVEN_GUITAR,
    DISTORTION_GUITAR, GUITAR_HARMONICS, ACOUSTIC_BASS,
    ELECTRIC_BASS_FINGER, ELECTRIC_BASS_PICK, FRETLESS_BASS,
    SLAP_BASS_1, SLAP_BASS_2, SYNTH_BASS_1, SYNTH_BASS_2, VIOLIN,
    VIOLA, CELLO, CONTRABASS, TREMOLO_STRINGS, PIZZICATO_STRINGS,
    ORCHESTRAL_STRINGS, TIMPANI, STRING_ENSEMBLE_1, STRING_ENSEMBLE_2,
    SYNTH_STRINGS_1, SYNTH_STRINGS_2, CHOIR_AAHS, VOICE_OOHS,
    SYNTH_VOICE, ORCHESTRA_HIT, TRUMPET, TROMBONE, TUBA,
    MUTED_TRUMPET, FRENCH_HORN, BRASS_SECTION, SYNTH_BRASS_1,
    SYNTH_BRASS_2, SOPRANO_SAX, ALTO_SAX, TENOR_SAX, BARITONE_SAX,
    OBOE, ENGLISH_HORN, BASSOON, CLARINET, PICCOLO, FLUTE, RECORDER,
    PAN_FLUTE, BLOWN_BOTTLE, SKAKUHACHI, WHISTLE, OCARINA, SQUARE,
    SAWTOOTH, CALLIOPE, CHIFF, CHARANG, VOICE, FIFTHS, BASSLEAD,
    NEW_AGE, WARM, POLYSYNTH, CHOIR, BOWED, METALLIC, HALO, SWEEP,
    RAIN, SOUNDTRACK, CRYSTAL, ATMOSPHERE, BRIGHTNESS, GOBLINS,
    ECHOES, SCI_FI, SITAR, BANJO, SHAMISEN, KOTO, KALIMBA, BAGPIPE,
    FIDDLE, SHANAI, TINKLE_BELL, AGOGO, STEEL_DRUMS, WOODBLOCK,
    TAIKO_DRUM, MELODIC_TOM, SYNTH_DRUM, REVERSE_CYMBAL,
    GUITAR_FRET_NOISE, BREATH_NOISE, SEASHORE, BIRD_TWEET,
    TELEPHONE_RING, HELICOPTER, APPLAUSE, GUNSHOT;

    @Override
    public String toString() {
        return ordinal() == 103 ? "SCI-FI" : super.toString();
    }

    /**
     * Returns a music string to set a track's instrument
     * @return Music string to set a track's instrument
     */
    public String toMusicString() {
        return String.format("I[%1$s] ", toString());
    }

    /**
     * Returns an instrument with the given MIDI number
     * @param instrument MIDI number of the instrument
     * @return Instrument with the given MIDI number
     */
    public static Instrument fromInt(int instrument) {
        return Instrument.values()[instrument];
    }

    /**
     * Returns the MIDI number of the instrument
     * @return MIDI number of the instrument
     */
    public int toInteger() {
        return ordinal();
    }
}
