package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.AbstractTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * One octave low tones for test purposes
 */
public class TestBassTones extends AbstractTones {
    public TestBassTones() {
        super(new MusicStringTone("E2"), new MusicStringTone("E3"));
    }
}
