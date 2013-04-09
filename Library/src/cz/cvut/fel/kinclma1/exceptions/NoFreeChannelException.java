package cz.cvut.fel.kinclma1.exceptions;

/**
 * Exception thrown when trying to add a track to a song and no midi channel is free to use
 */
public class NoFreeChannelException extends RuntimeException {
    /**
     * Creates a NoFreeChannelException given a message
     * @param message Message to be contained in the exception
     */
    public NoFreeChannelException(String message) {
        super(message);
    }
}
