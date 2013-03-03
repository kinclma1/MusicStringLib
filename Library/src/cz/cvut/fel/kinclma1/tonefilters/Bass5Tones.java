package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.InstrumentTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 24.2.13
 * Time: 23:58
 * To change this template use File | Settings | File Templates.
 */
public class Bass5Tones extends InstrumentTones {

    public Bass5Tones() {
        setRange(new MusicStringTone("B1"), new MusicStringTone("D#5"));
    }
}
