package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.AbstractTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 12.3.13
 * Time: 3:52
 * To change this template use File | Settings | File Templates.
 */
public class SoloTones extends AbstractTones {
    public SoloTones() {
        super(new MusicStringTone("C6"), new MusicStringTone("C7"));
    }
}
