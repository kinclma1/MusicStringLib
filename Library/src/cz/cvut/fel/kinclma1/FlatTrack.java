package cz.cvut.fel.kinclma1;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 21.1.13
 * Time: 0:43
 * To change this template use File | Settings | File Templates.
 */
class FlatTrack {
    private MusicStringDuration duration;
    private LinkedList<HashSet<String>> tones;

    FlatTrack(MusicStringDuration duration) {
        this.duration = duration;
        tones = new LinkedList<HashSet<String>>();
    }

    void addToneSet(HashSet<String> toneSet) {
        tones.add(toneSet);
    }

    void addMeasureDelimiter() {
        HashSet<String> set = new HashSet<String>(1);
        set.add("|");
        tones.add(set);
    }

    //todo tostring
}
