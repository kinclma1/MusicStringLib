package cz.cvut.fel.kinclma1;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 24.1.13
 * Time: 22:55
 * To change this template use File | Settings | File Templates.
 */
public abstract class NoteContent {

    public abstract int toInt();

    public abstract String toString();

    protected String relativeTone() {
        return null;
    }
}
