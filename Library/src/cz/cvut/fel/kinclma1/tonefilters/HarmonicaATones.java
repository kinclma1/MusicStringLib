package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.AbstractTones;

/**
 * Tones playable on a richter-tuned A scale harmonica
 */
public class HarmonicaATones extends AbstractTones {
    public HarmonicaATones() {
        super(new String[]{"A3","C#4","E4","A4","C#5","E5","A5","C#6","E6","A6",
                "B3","E4","G#4","B4","D5","F#5","G#5","B5","D6","F#6"});
    }
}
