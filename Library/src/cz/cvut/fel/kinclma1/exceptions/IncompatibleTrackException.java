package cz.cvut.fel.kinclma1.exceptions;

/**
 * Exception thrown when trying to add a track to a song and the length of measures does not match
 */
public class IncompatibleTrackException extends RuntimeException {
    public IncompatibleTrackException(String message) {
        super(message);
    }
}
