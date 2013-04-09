package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.AbstractTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * Tones playable on a 7 string guitar
 */
public class Guitar7Tones extends AbstractTones {

    public Guitar7Tones() {
        super(new MusicStringTone("B2"), new MusicStringTone("C7"));
    }
}
