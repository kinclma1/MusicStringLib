package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.AbstractTones;

/**
 * Tones playable on a richter-tuned C scale harmonica
 */
public class HarmonicaCTones extends AbstractTones {

    public HarmonicaCTones() {
        super(new String[]{"C4","E4","G4","C5","E5","G5","C6","E6","G6","C7",
                "D4","G4","B4","D5","F5","A5","B5","D6","F6","A6"});
    }
}
