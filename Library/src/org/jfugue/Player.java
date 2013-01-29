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
 *@see MidiRenderer
 *@see Pattern
 *@author David Koelle
 *@version 2.0
 */
public class Player
{
    private Sequencer sequencer;
    private MusicStringParser parser;
    private MidiRenderer renderer;
    private float sequenceTiming = Sequence.PPQ;
    private int resolution = 120;
    private boolean paused = false;
    private boolean started = false;
    private boolean finished = false;
    private long sequenceLength;

    /**
     * Instantiates a new Player object, which is used for playing music.
     */
    public Player()
    {
        try {
            // Get default sequencer.
            setSequencer(MidiSystem.getSequencer(true));
        } catch (MidiUnavailableException e)
        {
            throw new JFugueException(JFugueException.SEQUENCER_DEVICE_NOT_SUPPORTED_WITH_EXCEPTION + e.getMessage());
        }
        initParser();
    }

    private void setSequencer(Sequencer sequencer)
    {
        this.sequencer = sequencer;
        initSequencer();
    }

    private void initParser()
    {
        this.parser = new MusicStringParser();
        this.renderer = new MidiRenderer(sequenceTiming, resolution);
        this.parser.addParserListener(this.renderer);
    }
    
    private void initSequencer()
    {
        // Close the sequencer and synthesizer
        getSequencer().addMetaEventListener(new MetaEventListener() {
            public void meta(MetaMessage event)
            {
                if (event.getType() == 47)
                {
                    close();
                }
            }
        });
    }

    /**
     * Plays a string of music.  Be sure to call player.close() after play() has returned.
     * @param musicString the MusicString (JFugue-formatted string) to play
     * @version 3.0
     */
    public void play(String musicString)
    {
        if (musicString.indexOf(".mid") > 0)
        {
            // If the user tried to call this method with "filename.mid" or "filename.midi", throw the following exception
            throw new JFugueException(JFugueException.PLAYS_STRING_NOT_FILE_EXC);
        }

        play(getSequence(new Pattern(musicString)));
    }

    /**
     * Plays a MIDI Sequence
     * @param sequence the Sequence to play
     * @throws JFugueException if there is a problem playing the music
     * @see MidiRenderer
     */
    private void play(Sequence sequence)
    {
        sequenceLength = getSequenceLength(sequence);
        // Open the sequencer
        openSequencer();

        // Set the sequence
        try {
            getSequencer().setSequence(sequence);
        } catch (Exception e)
        {
            throw new JFugueException(JFugueException.ERROR_PLAYING_MUSIC + e.getMessage());
        }

        started = true;

        // Start the sequence
        sequencer.start();
    }

    private void openSequencer()
    {
        if (getSequencer() == null)
        {
            throw new JFugueException(JFugueException.SEQUENCER_DEVICE_NOT_SUPPORTED);
        }

        // Open the sequencer, if it is not already open
        if (!getSequencer().isOpen()) {
            try {
                getSequencer().open();
            } catch (MidiUnavailableException e)
            {
                throw new JFugueException(JFugueException.SEQUENCER_DEVICE_NOT_SUPPORTED_WITH_EXCEPTION + e.getMessage());
            }
        }
    }

    /**
     * Closes MIDI resources - be sure to call this after play() has returned.
     */
    public void close()
    {
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

    public boolean isStarted()
    {
        return this.started;
    }

    public boolean isFinished()
    {
        return this.finished;
    }

    public boolean isPlaying()
    {
        return getSequencer().isRunning();
    }

    public boolean isPaused()
    {
        return paused;
    }

    public void pause()
    {
        paused = true;
        if (isPlaying()) {
            getSequencer().stop();
        }
    }

    public void resume()
    {
        paused = false;
        getSequencer().start();
    }

    public void stop()
    {
        paused = false;
        getSequencer().stop();
        getSequencer().setMicrosecondPosition(0);
    }

    public void jumpTo(long microseconds)
    {
        getSequencer().setMicrosecondPosition(microseconds);
    }

    public void jumpTo(int percent) {
        jumpTo(sequenceLength / 100 * percent);
    }

    public long getSequenceLength(Sequence sequence)
    {
        return sequence.getMicrosecondLength();
    }

    public int getSequencePositionPercent() {
        return (int)(getSequencePosition() / sequenceLength);
    }

    public long getSequencePosition()
    {
        return getSequencer().getMicrosecondPosition();
    }

    /**
     * Returns the sequencer containing the MIDI data from a pattern that has been parsed.
     * @return the Sequencer from the pattern that was recently parsed
     */
    private Sequencer getSequencer()
    {
        return this.sequencer;
    }

    /**
     * Returns the sequence containing the MIDI data from the given pattern.
     * @return the Sequence from the given pattern
     */
    private Sequence getSequence(Pattern pattern)
    {
        this.renderer.reset();
        this.parser.parse(pattern);
        Sequence sequence = this.renderer.getSequence();
        return sequence;
    }
}