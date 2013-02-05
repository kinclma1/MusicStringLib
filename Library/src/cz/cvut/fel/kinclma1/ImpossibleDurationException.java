package cz.cvut.fel.kinclma1;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 5.2.13
 * Time: 22:23
 * To change this template use File | Settings | File Templates.
 */
public class ImpossibleDurationException extends Exception {
    public ImpossibleDurationException(List<Duration> durations) {
        super("Cannot create a single duration from the following durations: " + durations);
    }
}
