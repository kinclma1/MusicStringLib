package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.AbstractTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * Tones playable on a standard guitar
 */
public class GuitarTones extends AbstractTones {

    public GuitarTones() {
        super(new MusicStringTone("E3"), new MusicStringTone("C7"));
    }
}
