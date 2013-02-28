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

import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;

/**
 * This class takes a Pattern, and turns it into wonderful music.
 * <p/>
 * <p>
 * Playing music is only one thing that can be done by rendering a pattern.
 * You could also create your own renderer that draws sheet music based on
 * a pattern. Or, you could create a graphical light show based on the musical
 * notes in the pattern.
 * </p>
 * <p/>
 * <p>
 * This was named Renderer in previous versions of JFugue.  The name has been
 * changed to differentiate it from other types of renderers.
 * </p>
 *
 * @author David Koelle
 * @version 3.0 - Renderer renamed to MidiRenderer
 */
public final class MidiRenderer extends ParserListenerAdapter {
    private MidiEventManager eventManager;
    long initialNoteTime = 0;
    private float sequenceTiming;
    private int resolution;

    /**
     * Instantiates a Renderer
     */
    public MidiRenderer(float sequenceTiming, int resolution) {
        reset(sequenceTiming, resolution);
    }

    /**
     * Creates a new MidiEventManager.  If this isn't called,
     * events from multiple calls to render() will be added
     * to the same eventManager, which means that the second
     * time render() is called, it will contain music left over
     * from the first time it was called.  (This wasn't a problem
     * with Java 1.4)
     *
     * @since 3.0
     */
    public void reset(float sequenceTiming, int resolution) {
        this.sequenceTiming = sequenceTiming;
        this.resolution = resolution;
        eventManager = new MidiEventManager(sequenceTiming, resolution);
    }

    /**
     * Creates a new MidiEventManager using the sequenceTiming and
     * resolution already used to create this MidiRenderer.  If this
     * isn't called, events from multiple calls to render() will be
     * added to the same eventManager, which means that the second
     * time render() is called, it will contain music left over
     * from the first time it was called.  (This wasn't a problem
     * with Java 1.4)
     *
     * @since 3.2
     */
    public void reset() {
        eventManager = new MidiEventManager(sequenceTiming, resolution);
    }

    /**
     * Returns the last sequence generated by this renderer
     */
    public Sequence getSequence() {
        return eventManager.getSequence();
    }

    // ParserListener methods
    ////////////////////////////

    public void voiceEvent(byte voice) {
        eventManager.setCurrentTrack(voice);
    }

    public void tempoEvent(int tempo) {
        byte[] threeTempoBytes = TimeFactor.convertToThreeTempoBytes(tempo);
        eventManager.addMetaMessage(0x51, threeTempoBytes);
    }

    public void instrumentEvent(byte instrument) {
        eventManager.addEvent(ShortMessage.PROGRAM_CHANGE, instrument, 0);
    }

    public void measureEvent() {
        // No MIDI is generated when a measure indicator is identified.
    }

    public void channelPressureEvent(byte channelPressure) {
        eventManager.addEvent(ShortMessage.CHANNEL_PRESSURE, channelPressure);
    }

    public void noteEvent(Note note) {
        // Remember the current track time, so we can flip back to it
        // if there are other notes to play in parallel
        initialNoteTime = eventManager.getTrackTimer();
        long duration = note.getDuration();

        // If there is no duration, don't add this note to the event manager
        if (duration == 0) {
            return;
        }

        // Add messages to the track
        if (note.isRest()) {
            eventManager.advanceTrackTimer(duration);
        } else {
            initialNoteTime = eventManager.getTrackTimer();
            byte attackVelocity = note.getAttackVelocity();
            byte decayVelocity = note.getDecayVelocity();
            eventManager.addNoteEvent(note.getValue(), attackVelocity, decayVelocity, duration, !note.isEndOfTie(), !note.isStartOfTie());
        }
    }

    public void sequentialNoteEvent(Note note) {
        long duration = note.getDuration();
        if (note.isRest()) {
            eventManager.advanceTrackTimer(duration);
        } else {
            byte attackVelocity = note.getAttackVelocity();
            byte decayVelocity = note.getDecayVelocity();
            eventManager.addNoteEvent(note.getValue(), attackVelocity, decayVelocity, duration, !note.isEndOfTie(), !note.isStartOfTie());
        }
    }

    public void parallelNoteEvent(Note note) {
        long duration = note.getDuration();
        eventManager.setTrackTimer(initialNoteTime);
        if (note.isRest()) {
            eventManager.advanceTrackTimer(duration);
        } else {
            byte attackVelocity = note.getAttackVelocity();
            byte decayVelocity = note.getDecayVelocity();
            eventManager.addNoteEvent(note.getValue(), attackVelocity, decayVelocity, duration, !note.isEndOfTie(), !note.isStartOfTie());
        }
    }
}
