package cz.cvut.fel.kinclma1;

import java.util.HashSet;
import java.util.Iterator;
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

    Iterator<HashSet<String>> getIterator() {
        return tones.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator;
        String currTone;
        for (HashSet<String> beat : tones) {
            iterator = beat.iterator();
            while (iterator.hasNext()) {
                currTone = iterator.next();
                sb.append(currTone);
                if (!currTone.equals("|")) {
                    sb.append(duration.toString());
                }
                if (iterator.hasNext()) {
                    sb.append("+");
                }
            }
            sb.append(" ");
        }

        return sb.toString();
    }
}
