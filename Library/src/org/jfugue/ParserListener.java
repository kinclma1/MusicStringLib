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

import java.util.EventListener;

/**
 * Classes that implement ParserListener and add themselves as listeners
 * to a <code>Parser</code> object will receive events when
 * the <code>Parser</code> inteprets tokens from a Music String.
 *
 * @author David Koelle
 * @version 3.0
 * @see MusicStringParser
 */
public interface ParserListener extends EventListener {
    /**
     * Called when the parser encounters a voice event.
     *
     * @param voice the event that has been parsed
     */
    public void voiceEvent(byte voice);

    /**
     * Called when the parser encounters a tempo event.
     *
     * @param tempo the event that has been parsed
     */
    public void tempoEvent(int tempo);

    /**
     * Called when the parser encounters an instrument event.
     *
     * @param instrument the event that has been parsed
     */
    public void instrumentEvent(byte instrument);

    /**
     * Called when the parser encounters a measure event.
     */
    public void measureEvent();

    /**
     * Called when the parser encounters a channel pressure event.
     *
     * @param channelPressure the event that has been parsed
     */
    public void channelPressureEvent(byte channelPressure);

    /**
     * Called when the parser encounters an initial note event.
     *
     * @param note the event that has been parsed
     * @see Note
     */
    public void noteEvent(Note note);

    /**
     * Called when the parser encounters a sequential note event.
     *
     * @param note the event that has been parsed
     * @see Note
     */
    public void sequentialNoteEvent(Note note);

    /**
     * Called when the parser encounters a parallel note event.
     *
     * @param note the event that has been parsed
     * @see Note
     */
    public void parallelNoteEvent(Note note);
}
