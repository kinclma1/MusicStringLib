package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.AbstractTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * One octave high tones for test purposes
 */
public class SoloTones extends AbstractTones {
    public SoloTones() {
        super(new MusicStringTone("C6"), new MusicStringTone("C7"));
    }
}
