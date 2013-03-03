package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.InstrumentTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 24.2.13
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public class BassTones extends InstrumentTones {

    public BassTones() {
        setRange(new MusicStringTone("E2"), new MusicStringTone("D#5"));
    }
}
