package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.InstrumentTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 24.2.13
 * Time: 23:54
 * To change this template use File | Settings | File Templates.
 */
public class Guitar7Tones extends InstrumentTones {

    public Guitar7Tones() {
        super.setRange(new MusicStringTone("B2"), new MusicStringTone("C7"));
    }
}
