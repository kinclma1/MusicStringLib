package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.AbstractTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * Tones playable on a standard bass guitar
 */
public class BassTones extends AbstractTones {

    public BassTones() {
        super(new MusicStringTone("E2"), new MusicStringTone("D#5"));
    }
}
