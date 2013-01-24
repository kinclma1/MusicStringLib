package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.models.TGDuration;

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

    private MusicStringDuration(char dur) {
        duration =
                dur == 'w' ? Duration.WHOLE :
                dur == 'h' ? Duration.HALF :
                dur == 'q' ? Duration.QUARTER :
                dur == 'i' ? Duration.EIGHTH :
                dur == 's' ? Duration.SIXTEENTH :
                dur == 't' ? Duration.THIRTY_SECOND :
                dur == 'x' ? Duration.SIXTY_FOURTH :
                dur == 'o' ? Duration.ONE_TWENTY_EIGHTH : null;
    }

    public MusicStringDuration(String dur) {
        this(dur.charAt(0));
        dotted = dur.contains(".");
    }

    private MusicStringDuration(int dur) {
        duration =
                dur == 1 ? Duration.WHOLE :
                dur == 2 ? Duration.HALF :
                dur == 4 ? Duration.QUARTER :
                dur == 8 ? Duration.EIGHTH :
                dur == 16 ? Duration.SIXTEENTH :
                dur == 32 ? Duration.THIRTY_SECOND :
                dur == 64 ? Duration.SIXTY_FOURTH :
                dur == 128 ? Duration.ONE_TWENTY_EIGHTH : null;
    }

    public MusicStringDuration(TGDuration dur) {
        this(dur.getValue());
        dotted = dur.isDotted();
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
        return dotted ? new MusicStringDuration(shorter().toString()) : this;
    }

    private Duration shorter() {
        return duration == Duration.WHOLE ? Duration.HALF :
               duration == Duration.HALF ? Duration.QUARTER :
               duration == Duration.QUARTER ? Duration.EIGHTH :
               duration == Duration.EIGHTH ? Duration.SIXTEENTH :
               duration == Duration.SIXTEENTH ? Duration.THIRTY_SECOND :
               duration == Duration.THIRTY_SECOND ? Duration.SIXTY_FOURTH : Duration.ONE_TWENTY_EIGHTH;
    }

    @Override
    public int compareTo(MusicStringDuration d) {
        return this.toIntegerDiv128() - d.toIntegerDiv128();
    }
}
