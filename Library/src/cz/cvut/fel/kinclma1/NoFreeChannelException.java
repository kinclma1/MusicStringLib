package cz.cvut.fel.kinclma1;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 10.3.13
 * Time: 22:25
 * To change this template use File | Settings | File Templates.
 */
public class NoFreeChannelException extends RuntimeException {
    public NoFreeChannelException(String message) {
        super(message);
    }
}
