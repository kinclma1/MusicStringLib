package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.models.TGDuration;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 19.11.12
 * Time: 20:10
 * To change this template use File | Settings | File Templates.
 */
class MusicStringDuration implements Comparable<MusicStringDuration> {
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

    private MusicStringDuration(int dur) {
        duration = intDurationMap.get(dur);
    }

    public MusicStringDuration(TGDuration dur) {
        this(dur.getValue());
        dotted = dur.isDotted();
    }

    public MusicStringDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return dotted ? duration.toString() + "." : duration.toString();
    }

    public int toInteger() {
        return duration.toInteger();
    }

    public boolean isDotted() {
        return dotted;
    }

    public int toIntegerDiv128() {
        int tmp = 128 / toInteger();
        return dotted ? tmp + tmp / 2 : tmp;
    }

    MusicStringDuration shortest() {
        return dotted ? new MusicStringDuration(shorter()) : this;
    }

    private Duration shorter() {
        return shorterDurationMap.get(duration);
    }

    @Override
    public int compareTo(MusicStringDuration d) {
        return this.toIntegerDiv128() - d.toIntegerDiv128();
    }
}
