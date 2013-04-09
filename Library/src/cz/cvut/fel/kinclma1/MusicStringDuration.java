package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.exceptions.ImpossibleDurationException;
import org.herac.tuxguitar.song.models.TGDuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Wrapper class for the Duration enum providing additional features
 */
public class MusicStringDuration implements Comparable<MusicStringDuration> {
    private boolean dotted;
    private Duration duration;

    private static HashMap<Character, Duration> charDurationMap;
    private static HashMap<Integer, Duration> intDurationMap;
    private static HashMap<Duration, Duration> shorterDurationMap;

    static {
        charDurationMap = new HashMap<Character, Duration>(8);
        charDurationMap.put('w', Duration.WHOLE);
        charDurationMap.put('h', Duration.HALF);
        charDurationMap.put('q', Duration.QUARTER);
        charDurationMap.put('i', Duration.EIGHTH);
        charDurationMap.put('s', Duration.SIXTEENTH);
        charDurationMap.put('t', Duration.THIRTY_SECOND);
        charDurationMap.put('x', Duration.SIXTY_FOURTH);
        charDurationMap.put('o', Duration.ONE_TWENTY_EIGHTH);

        intDurationMap = new HashMap<Integer, Duration>(8);
        intDurationMap.put(1, Duration.WHOLE);
        intDurationMap.put(2, Duration.HALF);
        intDurationMap.put(4, Duration.QUARTER);
        intDurationMap.put(8, Duration.EIGHTH);
        intDurationMap.put(16, Duration.SIXTEENTH);
        intDurationMap.put(32, Duration.THIRTY_SECOND);
        intDurationMap.put(64, Duration.SIXTY_FOURTH);
        intDurationMap.put(128, Duration.ONE_TWENTY_EIGHTH);

        shorterDurationMap = new HashMap<Duration, Duration>(7);
        shorterDurationMap.put(Duration.WHOLE, Duration.HALF);
        shorterDurationMap.put(Duration.HALF, Duration.QUARTER);
        shorterDurationMap.put(Duration.QUARTER, Duration.EIGHTH);
        shorterDurationMap.put(Duration.EIGHTH, Duration.SIXTEENTH);
        shorterDurationMap.put(Duration.SIXTEENTH, Duration.THIRTY_SECOND);
        shorterDurationMap.put(Duration.THIRTY_SECOND, Duration.SIXTY_FOURTH);
        shorterDurationMap.put(Duration.SIXTY_FOURTH, Duration.ONE_TWENTY_EIGHTH);
    }

    /**
     * Parses the duration from a music string starting with the given index,
     * if no duration found, sets quarter
     * @param musicString Music string containing a duration
     * @param minIndex Index where to begin the search
     */
    public MusicStringDuration(String musicString, int minIndex) {
        boolean found = false;
        for (int i = minIndex; !found && i < musicString.length(); i ++) {
            Duration d = charDurationMap.get(musicString.charAt(i));
            if (d != null) {
                duration = d;
                found = true;
            }
        }

        if (!found) {
            duration = Duration.QUARTER;
        }

        dotted = musicString.contains(".");
    }

    /**
     * Creates a duration from a TuxGuitar duration
     * @param dur TGDuration from which this should be created
     */
    public MusicStringDuration(TGDuration dur) {
        duration = intDurationMap.get(dur.getValue());
        dotted = dur.isDotted();
    }

    /**
     * Create a duration from a Duration enum value
     * @param duration Value to set
     */
    public MusicStringDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * Tries to create a duration from the given proportional integer
     * @param proportional proportional duration value
     * @throws ImpossibleDurationException when not possible to create a single duration from the given integer
     */
    public MusicStringDuration(int proportional) throws ImpossibleDurationException {
        int i = 128;
        boolean set = false;
        while (i >= 1 && !set) {
            if (i <= proportional) {
                duration = intDurationMap.get(128 / i);
                proportional -= i;
                set = true;
            }
            i /= 2;
            if (set && proportional == i && proportional > 0) {
                dotted = true;
                proportional -= i;
            }
        }

        if (proportional > 0) {
            List<Duration> durations = new ArrayList<Duration>();
            durations.add(duration);
            if (dotted) {
                durations.add(shorter());
            }

            while (i > 0 && proportional > 0) {
                if (i <= proportional) {
                    durations.add(intDurationMap.get(128 / i));
                    proportional -= i;
                }
                i /= 2;
            }
            throw new ImpossibleDurationException(durations);
        }
    }

    @Override
    public MusicStringDuration clone() {
        MusicStringDuration newDuration = new MusicStringDuration(duration);
        newDuration.dotted = dotted;
        return newDuration;
    }

    @Override
    public String toString() {
        return dotted ? duration.toString() + '.' : duration.toString();
    }

    /**
     * Returns non-proportional int value
     * @return non-proportional int value
     */
    public int toInteger() {
        return duration.toInteger();
    }

    public boolean isDotted() {
        return dotted;
    }

    /**
     * Returns proportional int value
     * @return proportional int value
     */
    public int toProportionalInt() {
        int tmp = 128 / toInteger();
        return dotted ? tmp + tmp / 2 : tmp;
    }

    /**
     * Returns the duration resolution - value of this if not dotted, else half of this
     * @return duration resolution - value of this if not dotted, else half of this
     */
    MusicStringDuration shortest() {
        return dotted ? new MusicStringDuration(shorter()) : this;
    }

    private Duration shorter() {
        return shorterDurationMap.get(duration);
    }

    @Override
    public int compareTo(MusicStringDuration d) {
        return toProportionalInt() - d.toProportionalInt();
    }
}
