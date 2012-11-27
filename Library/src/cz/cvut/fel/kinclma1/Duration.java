package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.models.TGDuration;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 24.9.12
 * Time: 19:26
 * To change this template use File | Settings | File Templates.
 */
public enum Duration {
    WHOLE,
    HALF,
    QUARTER,
    EIGHTH,
    SIXTEENTH,
    THIRTY_SECOND,
    SIXTY_FOURTH,
    ONE_TWENTY_EIGHTH;

    @Override
    public String toString() {
        switch (this) {
            case WHOLE:
                return "w";
            case HALF:
                return "h";
            case QUARTER:
                return "q";
            case EIGHTH:
                return "i";
            case SIXTEENTH:
                return "s";
            case THIRTY_SECOND:
                return "t";
            case SIXTY_FOURTH:
                return "x";
            case ONE_TWENTY_EIGHTH:
                return "o";

        }
    }

    public int toInteger() {
        switch (this) {
            case WHOLE:
                return 1;
            case HALF:
                return 2;
            case QUARTER:
                return 4;
            case EIGHTH:
                return 8;
            case SIXTEENTH:
                return 16;
            case THIRTY_SECOND:
                return 32;
            case SIXTY_FOURTH:
                return 64;
            case ONE_TWENTY_EIGHTH:
                return 128;
            default:
                return 0;
        }
    }
}
