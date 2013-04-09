package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.exceptions.ImpossibleDurationException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Simple representation of a track with fixed note duration
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

    /**
     * Returns an Iterator for the track beats
     * @return Iterator for the track beats
     */
    public Iterator<HashSet<String>> getIterator() {
        return tones.iterator();
    }

    /**
     * Returns the duration of the track notes
     * @return Duration of the track notes
     */
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
