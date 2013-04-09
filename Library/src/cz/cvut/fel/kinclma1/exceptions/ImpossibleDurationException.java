package cz.cvut.fel.kinclma1.exceptions;

import cz.cvut.fel.kinclma1.Duration;

import java.util.List;

/**
 * Exception thrown by the constructor of MusicStringDuration taking integer argument, when it is not possible to
 * create a single note duration from the integer given
 */
public class ImpossibleDurationException extends Exception {

    private List<Duration> durations;

    /**
     * Creates ImpossibleDurationException from the given list of durations
     * @param durations List of durations representing the integer value given to the MusicStringDuration constructor
     */
    public ImpossibleDurationException(List<Duration> durations) {
        super("Cannot create a single duration from the following durations: " + durations);
        this.durations = durations;
    }

    /**
     * Gets the list of durations given to the constructor
     * @return list of durations given to the constructor
     */
    public List<Duration> getDurations() {
        return durations;
    }
}
