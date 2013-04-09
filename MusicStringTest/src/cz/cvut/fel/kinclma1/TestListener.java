package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.player.MusicStringPlayer;
import cz.cvut.fel.kinclma1.player.PlayerListener;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 30.1.13
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class TestListener implements PlayerListener {

    @Override
    public void setPlayer(MusicStringPlayer player) {

    }

    @Override
    public void setSongLength(int seconds) {
        System.out.println("Song length: " + seconds);
    }

    @Override
    public void setPlaying(boolean playing) {
        System.out.println(playing ? "Playing" : "Paused");
    }

    @Override
    public void setPosition(int seconds) {
        System.out.println("Current position: " + seconds);
    }

    @Override
    public void close() {
        System.out.println("Closing player.");
    }
}
