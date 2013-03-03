package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.InstrumentTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 26.2.13
 * Time: 0:32
 * To change this template use File | Settings | File Templates.
 */
public class TestTones extends InstrumentTones {
    public TestTones() {
        setRange(new MusicStringTone("E4"), new MusicStringTone("D#5"));
    }
}
