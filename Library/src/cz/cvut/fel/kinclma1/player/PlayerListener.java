package cz.cvut.fel.kinclma1.player;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 30.1.13
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */

/**
 * An event listener to a MusicStringPlayer
 */
public interface PlayerListener {

    /**
     * Sets player to send commands to
     * @param player controlled MusicStringPlayer instance
     */
    public void setPlayer(MusicStringPlayer player);

    /**
     * Sets song length in the listener, only for initialization purposes
     * @param length length of the song in seconds
     */
    public void setSongLength(int length);

    /**
     * Sets the current playing state of the player (playing/paused) to the listener
     * @param playing state of playback true = player is playing, false = player is paused
     */
    public void setPlaying(boolean playing);

    /**
     * Sets the current player position in the song to the listener
     * @param position current playback position in seconds
     */
    public void setPosition(int position);

    /**
     * Closes the listener, when its associated player gets closed
     */
    public void close();
}
