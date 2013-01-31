package cz.cvut.fel.kinclma1.player;

import org.jfugue.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 30.1.13
 * Time: 22:28
 * To change this template use File | Settings | File Templates.
 */
public class MusicStringPlayer {

    private class NotifyTask extends TimerTask {

        @Override
        public void run() {
            for (PlayerListener listener : listeners) {
                listener.setPlaying(player.isPlaying());
                listener.setPosition(player.getSequencePositionSeconds());
            }
        }
    }

    private List<PlayerListener> listeners;
    private Player player;
    private Timer timer;

    public MusicStringPlayer(String musicString, List<PlayerListener> listeners) {
        this.listeners = listeners == null ? new ArrayList<PlayerListener>() : listeners;
        init(musicString);
    }

    public MusicStringPlayer(String musicString, PlayerListener listener) {
        this.listeners = new ArrayList<PlayerListener>(1);
        if (listener != null) {
            listeners.add(listener);
        }
        init(musicString);
    }

    private void init(String musicString) {
        this.player = new Player(musicString == null ? "" : musicString);
        timer = new Timer();
        initListeners();
        timer.schedule(new NotifyTask(), 500, 500);
    }

    private void initListeners() {
        for (PlayerListener listener : listeners) {
            listener.setSongLength(player.getSequenceLengthSeconds());
            listener.setPlaying(false);
            listener.setPosition(0);
        }
    }

    public void play() {
        if (!player.isStarted()) {
            player.play();
        } else if (player.isPaused()) {
            player.resume();
        }
    }

    public void pause() {
        player.pause();
    }

    public void stop() {
        player.stop();
    }

    public void close() {
        player.stop();
        timer.cancel();
        player.close();
    }

    public void setPosition(int seconds) {
        player.jumpTo(seconds);
    }
}
