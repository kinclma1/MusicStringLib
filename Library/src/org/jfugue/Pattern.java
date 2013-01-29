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

import sun.misc.Regexp;
import sun.org.mozilla.javascript.internal.ast.RegExpLiteral;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.swing.event.EventListenerList;

/**
 * This class represents a segment of music.  By representing segments of music
 * as patterns, JFugue gives users the opportunity to play around with pieces
 * of music in new and interesting ways.  Patterns may be added together, transformed,
 * or otherwise manipulated to expand the possibilities of creative music.
 *
 * @author David Koelle
 * @version 2.0
 * @version 4.0 - Added Pattern Properties
 * @version 4.0.3 - Now implements Serializable
 */
public class Pattern implements Serializable
{
    private String musicString;

    /**
     * Instantiates a new pattern using the given music string
     * @param musicString the music string
     */
    public Pattern(String musicString)
    {
        this.musicString = musicString;
    }

    /**
     * Returns an array of strings representing each token in the Pattern.
     * @return
     */
    public String[] getTokens()
    {
        return musicString.split("\\s+");
    }
}
