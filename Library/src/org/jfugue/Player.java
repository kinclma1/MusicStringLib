/*
 * JFugue - API for Music Programming
 * Copyright (C) 2003-2008  David Koelle
 *
 * http://www.jfugue.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.jfugue;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

/**
 * Prepares a pattern to be turned into music by the Renderer.  This class
 * also handles saving the sequence derived from a pattern as a MIDI file.
 *
 * @author David Koelle
 * @version 2.0
 * @see MidiRenderer
 * @see Pattern
 */
public class Player {
    private Sequencer sequencer;
    private MusicStringParser parser;
    private MidiRenderer renderer;
    private float sequenceTiming = Sequence.PPQ;
    private int resolution = 120;
    private boolean paused = false;
    private boolean started = false;
    private boolean finished = false;
    private long sequenceLength;
    private static final int MILLION = 1000000;
    private Sequence sequence;

    /**
     * Instantiates a new Player object, which is used for playing music.
     */
    private Player() {
        try {
            // Get default sequencer.
            setSequencer(MidiSystem.getSequencer(true));
        } catch (MidiUnavailableException e) {
            throw new JFugueException(JFugueException.SEQUENCER_DEVICE_NOT_SUPPORTED_WITH_EXCEPTION + e.getMessage());
        }
        initParser();
    }

    public Player(String musicString) {
        this();
        initSequence(musicString);

        initSequencer();
    }

    private void initSequencer() {
        openSequencer();

        try {
            sequencer.setSequence(sequence);
        } catch (Exception e) {
            throw new JFugueException(JFugueException.ERROR_PLAYING_MUSIC + e.getMessage());
        }
    }

    private void setSequencer(Sequencer sequencer) {
        this.sequencer = sequencer;
//        setCloseWhenFinished();
    }

    private void initParser() {
        this.parser = new MusicStringParser();
        this.renderer = new MidiRenderer(sequenceTiming, resolution);
        this.parser.addParserListener(this.renderer);
    }

    private void setCloseWhenFinished() {
        // Close the sequencer and synthesizer
        sequencer.addMetaEventListener(new MetaEventListener() {
            public void meta(MetaMessage event) {
                if (event.getType() == 47) {
                    close();
                }
            }
        });
    }

//    /**
//     * Plays a string of music.  Be sure to call player.close() after play() has returned.
//     *
//     * @param musicString the MusicString (JFugue-formatted string) to play
//     * @version 3.0
//     */
//    public void play(String musicString) {
//        if (musicString.indexOf(".mid") > 0) {
//            // If the user tried to call this method with "filename.mid" or "filename.midi", throw the following exception
//            throw new JFugueException(JFugueException.PLAYS_STRING_NOT_FILE_EXC);
//        }
//        initSequence(musicString);
//        play();
//    }

    private void initSequence(String musicString) {
        if (musicString.indexOf(".mid") > 0) {
            // If the user tried to call this method with "filename.mid" or "filename.midi", throw the following exception
            throw new JFugueException(JFugueException.PLAYS_STRING_NOT_FILE_EXC);
        }

        this.sequence = getSequence(new Pattern(musicString));
        sequenceLength = getSequenceLength(sequence);
    }

    /**
     * Plays a MIDI Sequence
     *
     * @throws JFugueException if there is a problem playing the music
     * @see MidiRenderer
     */
    public void play() {
        started = true;

        // Start the sequence
        sequencer.start();
    }

    private void openSequencer() {
        if (sequencer == null) {
            throw new JFugueException(JFugueException.SEQUENCER_DEVICE_NOT_SUPPORTED);
        }

        // Open the sequencer, if it is not already open
        if (!sequencer.isOpen()) {
            try {
                sequencer.open();
            } catch (MidiUnavailableException e) {
                throw new JFugueException(JFugueException.SEQUENCER_DEVICE_NOT_SUPPORTED_WITH_EXCEPTION + e.getMessage());
            }
        }
    }

    /**
     * Closes MIDI resources - be sure to call this after play() has returned.
     */
    public void close() {
        finished = true;
        sequencer.close();
        try {
            if (MidiSystem.getSynthesizer() != null) {
                MidiSystem.getSynthesizer().close();
            }
        } catch (MidiUnavailableException e) {
            throw new JFugueException(JFugueException.GENERAL_ERROR + e.getMessage());
        }
    }

    public boolean isStarted() {
        return this.started;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isPlaying() {
        return sequencer.isRunning();
    }

    public boolean isPaused() {
        return paused;
    }

    public void pause() {
        paused = true;
        if (isPlaying()) {
            sequencer.stop();
        }
    }

    public void resume() {
        paused = false;
        sequencer.start();
    }

    public void stop() {
        paused = true;
        sequencer.stop();
        sequencer.setMicrosecondPosition(0);
    }

    private void jumpTo(long microseconds) {
        sequencer.setMicrosecondPosition(microseconds);
    }

    public void jumpTo(int seconds) {
        jumpTo((long)MILLION * (long)seconds);
    }

    private long getSequenceLength(Sequence sequence) {
        return sequence.getMicrosecondLength();
    }

    public int getSequenceLengthSeconds() {
        return (int) (sequenceLength / MILLION);
    }

    public int getSequencePositionSeconds() {
        return (int) (getSequencePosition() / MILLION);
    }

    private long getSequencePosition() {
        return sequencer.getMicrosecondPosition();
    }

    /**
     * Returns the sequence containing the MIDI data from the given pattern.
     *
     * @return the Sequence from the given pattern
     */
    private Sequence getSequence(Pattern pattern) {
        this.renderer.reset();
        this.parser.parse(pattern);
        Sequence sequence = this.renderer.getSequence();
        return sequence;
    }
}