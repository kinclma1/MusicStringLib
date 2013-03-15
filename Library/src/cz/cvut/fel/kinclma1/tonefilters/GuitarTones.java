package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.AbstractTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 24.2.13
 * Time: 23:47
 * To change this template use File | Settings | File Templates.
 */
public class GuitarTones extends AbstractTones {

    public GuitarTones() {
        super(new MusicStringTone("E3"), new MusicStringTone("C7"));
    }
}
