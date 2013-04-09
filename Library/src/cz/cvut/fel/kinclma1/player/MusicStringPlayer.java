package cz.cvut.fel.kinclma1.player;

import org.jfugue.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Wrapper class for the customised JFugue Player class, which is a controller between the player and the
 * PlayerListeners
 */
public class MusicStringPlayer {

    private class NotifyTask extends TimerTask {

        @Override
        public void run() {
            notifyPosition();
        }
    }

    private List<PlayerListener> listeners;
    private Player player;
    private Timer timer;

    /**
     * Creates a new player for the given music string and registers and informs the given PlayerListeners
     * @param musicString The music string to be played
     * @param listeners List of the PlayerListeners to be interacting with the player
     */
    public MusicStringPlayer(String musicString, List<PlayerListener> listeners) {
        this.listeners = listeners == null ? new ArrayList<PlayerListener>() : listeners;
        init(musicString);
    }

    /**
     * Creates a new player for the given music string and registers and informs the given PlayerListener
     * @param musicString The music string to be played
     * @param listener PlayerListener to be interacting with the player
     */
    public MusicStringPlayer(String musicString, PlayerListener listener) {
        listeners = new ArrayList<PlayerListener>(1);
        if (listener != null) {
            listeners.add(listener);
        }
        init(musicString);
    }

    /**
     * Registers an additional PlayerListener
     * @param listener PlayerListener to be added to the list of listeners
     */
    public void addListener(PlayerListener listener) {
        listener.setPlayer(this);
        listener.setSongLength(player.getSequenceLengthSeconds());
        notifyPlaybackStatus();
        notifyPosition();
        listeners.add(listener);
    }

    /**
     * Removes the given PlayerListeners from the list of listeners
     * @param listener PlayerListener to be removed from the listener list
     */
    public void removeListener(PlayerListener listener) {
        listeners.remove(listener);
    }

    private void init(String musicString) {
        player = new Player(musicString == null ? "" : musicString);
        player.setMusicStringPlayer(this);
        timer = new Timer();
        initListeners();
        timer.schedule(new NotifyTask(), 500L, 500L);
    }

    private void initListeners() {
        for (PlayerListener listener : listeners) {
            listener.setPlayer(this);
            listener.setSongLength(player.getSequenceLengthSeconds());
            listener.setPlaying(false);
            listener.setPosition(0);
        }
    }

    private void notifyPlaybackStatus() {
        for (PlayerListener listener : listeners) {
            listener.setPlaying(player.isPlaying());
        }
    }

    private void notifyPosition() {
        for (PlayerListener listener : listeners) {
            listener.setPosition(player.getSequencePositionSeconds());
        }
    }

    private void closeListeners() {
        for (PlayerListener listener : listeners) {
            listener.close();
        }
        listeners.clear();
    }

    /**
     * Starts/resumes the playback
     */
    public void play() {
        if (!player.isStarted()) {
            player.play();
        } else if (player.isPaused()) {
            player.resume();
        }
        notifyPlaybackStatus();
    }

    /**
     * Pauses the playback
     */
    public void pause() {
        player.pause();
        notifyPlaybackStatus();
    }

    /**
     * Stops the playback
     */
    public void stop() {
        player.stop();
        notifyPlaybackStatus();
        notifyPosition();
    }

    /**
     * Cancels everything concerning the player and notifies listeners to close
     */
    public void close() {
        player.stop();
        timer.cancel();
        closeListeners();
        player.close();
    }

    /**
     * Sets the position of the playback in seconds from the start of the sequence
     * @param seconds Playback position to be set
     */
    public void setPosition(int seconds) {
        player.jumpTo(seconds);
        notifyPosition();
    }
}
