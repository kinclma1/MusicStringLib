package cz.cvut.fel.kinclma1;

/**
 * Public interface of tone filters
 */
public interface InstrumentTones {
    /**
     * Returns a FlatTrack containing the intersection of original tones and filter tones in all octaves of the filter
     * @param original Original flat track containing tones without octave numbers
     * @return New flat track containing the intersection of original tones and filter tones in all octaves of the filter
     */
    public FlatTrack filterTones(FlatTrack original);
}
