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

/**
 * This Adapter class implements all of the methods of
 * ParserListener, but the implementations are blank.
 * If you want something to be a ParserListener, but you don't
 * want to implement all of the ParserListener methods, extend
 * this class.
 *
 * @author David Koelle
 * @version 3.0
 */
public class ParserListenerAdapter implements ParserListener {
    /**
     * Called when the parser encounters a voice event.
     *
     * @param voice the event that has been parsed
     */
    @Override
    public void voiceEvent(byte voice) {
    }

    /**
     * Called when the parser encounters a tempo event.
     *
     * @param tempo the event that has been parsed
     */
    @Override
    public void tempoEvent(int tempo) {
    }

    /**
     * Called when the parser encounters an instrument event.
     *
     * @param instrument the event that has been parsed
     */
    @Override
    public void instrumentEvent(byte instrument) {
    }

    /**
     * Called when the parser encounters a measure event.
     */
    @Override
    public void measureEvent() {
    }

    /**
     * Called when the parser encounters a channel pressure event.
     *
     * @param channelPressure the event that has been parsed
     */
    @Override
    public void channelPressureEvent(byte channelPressure) {
    }

    /**
     * Called when the parser encounters an initial note event.
     *
     * @param note the event that has been parsed
     * @see Note
     */
    @Override
    public void noteEvent(Note note) {
    }

    /**
     * Called when the parser encounters a sequential note event.
     *
     * @param note the event that has been parsed
     * @see Note
     */
    @Override
    public void sequentialNoteEvent(Note note) {
    }

    /**
     * Called when the parser encounters a parallel note event.
     *
     * @param note the event that has been parsed
     * @see Note
     */
    @Override
    public void parallelNoteEvent(Note note) {
    }
}
