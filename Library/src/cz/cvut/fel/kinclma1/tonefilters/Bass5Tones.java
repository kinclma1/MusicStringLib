package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.AbstractTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * Tones playable on a 5 string bass guitar
 */
public class Bass5Tones extends AbstractTones {

    public Bass5Tones() {
        super(new MusicStringTone("B1"), new MusicStringTone("D#5"));
    }
}
