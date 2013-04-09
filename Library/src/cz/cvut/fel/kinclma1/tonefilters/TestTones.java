package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.AbstractTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * One octave medium tones for test purposes
 */
public class TestTones extends AbstractTones {
    public TestTones() {
        super(new MusicStringTone("E4"), new MusicStringTone("D#5"));
    }
}
