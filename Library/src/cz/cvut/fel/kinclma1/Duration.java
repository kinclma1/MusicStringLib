package cz.cvut.fel.kinclma1;

/**
 * Enum of base durations allowed in music string
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

    /**
     * Returns a single character string representation of the duration
     * @return Single character string representation of the duration
     */
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
            default:
                return null;
        }
    }

    /**
     * Returns an int value of the duration
     * @return Int value of the duration
     */
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
