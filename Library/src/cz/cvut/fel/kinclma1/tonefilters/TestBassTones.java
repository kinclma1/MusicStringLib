package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.AbstractTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 16.3.13
 * Time: 23:29
 * To change this template use File | Settings | File Templates.
 */
public class TestBassTones extends AbstractTones {
    public TestBassTones() {
        super(new MusicStringTone("E2"), new MusicStringTone("E3"));
    }
}
