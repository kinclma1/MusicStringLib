package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.exceptions.ImpossibleDurationException;

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
public class FlatTrack {
    private MusicStringDuration duration;
    private LinkedList<HashSet<String>> tones;

    FlatTrack(MusicStringDuration duration) {
        this.duration = duration;
        tones = new LinkedList<HashSet<String>>();
    }

    @Override
    public FlatTrack clone() {
        FlatTrack track = new FlatTrack(duration.clone());
        for (HashSet<String> beat : tones) {
            HashSet<String> newBeat = new HashSet<String>(beat.size());
            newBeat.addAll(beat);
            track.addToneSet(newBeat);
        }
        return track;
    }

    void addToneSet(HashSet<String> toneSet) {
        tones.add(toneSet);
    }

    void addMeasureDelimiter() {
        HashSet<String> set = new HashSet<String>(1);
        set.add("|");
        tones.add(set);
    }

    public Iterator<HashSet<String>> getIterator() {
        return tones.iterator();
    }

    public MusicStringDuration getDuration() {
        return duration;
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
                    sb.append('+');
                }
            }
            sb.append(' ');
        }

        return sb.toString();
    }
}
