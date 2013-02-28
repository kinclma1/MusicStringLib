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

import java.util.HashMap;
import java.util.Map;

/**
 * Parses music strings, and fires events for <code>ParserListener</code> interfaces
 * when tokens are interpreted. The <code>ParserListener</code> does intelligent things
 * with the resulting events, such as create music, draw sheet music, or
 * transform the data.
 * <p/>
 * As of Version 3.0, the Parser supports turning MIDI Sequences into JFugue Patterns with the parse(Sequence)
 * method.  In this case, the ParserListeners established by a ParserBuilder use the parsed
 * events to construct the Pattern string.
 *
 * @author David Koelle
 * @version 4.0 - Note parsing split up into many separate methods; verification added for testing purposes
 */
public final class MusicStringParser extends Parser {
    private Map<String, Object> dictionaryMap;
    private byte keySig = 0;

    /**
     * Creates a new Parser object, and populates the dictionary with initial entries.
     *
     * @see JFugueDefinitions
     */
    public MusicStringParser() {
        dictionaryMap = new HashMap<String, Object>();
        JFugueDefinitions.populateDictionary(dictionaryMap);
    }


    /**
     * Parses a <code>Pattern</code> and fires events to subscribed <code>ParserListener</code>
     * interfaces.  As the Pattern is parsed, events are sent
     * to <code>ParserLisener</code> interfaces, which are responsible for doing
     * something interesting with the music data, such as playing the music,
     * displaying it as sheet music, or transforming the pattern.
     * <p/>
     * <p>
     * The parser breaks a music string into tokens, which are separated by spaces.
     * It then determines the type of command based on the first character of the
     * token.  If the parser does not recognize the first character of the token,
     * which is limited to the command letters (K, V, T, I, L, X, #, $, @, &, +, *, |),
     * the notes (A, B, C, D, E, F, G, R),
     * and the open-bracket character ( [ ), then the token will be ignored.
     * </p>
     *
     * @param pattern the <code>Pattern</code> to parse
     * @throws Exception if there is an error parsing the pattern
     */
    public void parse(Pattern pattern) throws JFugueException {
        String[] tokens = pattern.getTokens();

        // If the user hasn't specified a tempo as the first token, use the default of 120
        if (tokens.length > 0) {
            if (tokens[0].toUpperCase().charAt(0) != 'T') {
                parseTempoElement("T120");
            }
        }

        int counter = 0;
        for (int t = 0; t < tokens.length; t++) {
            parseToken(tokens[t]);
            counter++;
            fireProgressReported("Parsing music string...", counter, tokens.length);
        }
    }

    /**
     * This method takes a single token, and distributes it to a specific
     * element parser based on the first character in the string.
     * If the parser does not recognize the first character of the string,
     * the token will be ignored.
     *
     * @param s the single token to parse
     * @throws JFugueException if there is a problem parsing the string
     */
    private void parseToken(String s) throws JFugueException {
        // If there are any spaces, get out
        if (s.indexOf(' ') != -1) {
            throw new JFugueException(JFugueException.PARSER_SPACES_EXC, s, s);
        }

        s = s.toUpperCase();
        trace("--------Processing Token: ", s);

        switch (s.charAt(0)) {
            case 'V':
                parseVoiceElement(s);
                break;
            case 'T':
                parseTempoElement(s);
                break;
            case 'I':
                parseInstrumentElement(s);
                break;
            case '+':
                parseChannelPressureElement(s);
                break;  // New in 3.0
            case '|':
                parseMeasureElement(s);
                break;  // New in 3.0
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'R':
            case '[':
                parseNoteElement(s);
                break;
            default:
                break;  // Unknown characters are okay
        }
    }

    /**
     * Parses a voice element.
     *
     * @param s the token that contains a voice element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseVoiceElement(String s) throws JFugueException {
        String voiceNumberString = s.substring(1, s.length());
        byte voiceNumber = getByteFromDictionary(voiceNumberString);
        if (voiceNumber > 15) {
//            throw new JFugueException(JFugueException.VOICE_EXC,voiceNumberString,s);
            return;
        }
        trace("Voice element: voice = ", voiceNumber);
        fireVoiceEvent(voiceNumber);
    }

    /**
     * Parses a tempo element.
     * As of JFugue 4.0, Tempo can be specified in Beats Per Minute, which is much more intuitive than
     * the original Milliseconds Per Quarter Note.  To maintain compatibility with existing JFugue
     * Music Strings, those wishing to specify Tempo using BPM need to use the full word "Tempo" in
     * their music string, instead of just the initial "T".
     * To summarize:
     * "Tempo120" (or "Tempo[Allegro]") --> Tempo will be is 120 beats per minute
     * "T120" --> Tempo will be 120 milliseconds per beat.  Divide into 60000000 to get BPM.
     *
     * @param s the token that contains a tempo element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseTempoElement(String s) throws JFugueException {
        String tempoNumberString = s.substring(1, s.length());
        int tempoNumber = getIntFromDictionary(tempoNumberString);
        trace("Tempo element: tempo = ", tempoNumber);
        fireTempoEvent(tempoNumber);
    }

    /**
     * Parses an instrument element.
     *
     * @param s the token that contains an instrument element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseInstrumentElement(String s) throws JFugueException {
        String instrumentNumberString = s.substring(1, s.length());
        byte instrumentNumber = getByteFromDictionary(instrumentNumberString);
        trace("Instrument element: instrument = ", instrumentNumber);
        fireInstrumentEvent(instrumentNumber);
    }

    /**
     * Parses a measure element.
     *
     * @param s the token that contains a measure element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseMeasureElement(String s) throws JFugueException {
        trace("Measure element.");
        fireMeasureEvent();
    }

    /**
     * Parses a channel pressure element.
     *
     * @param s the token that contains a channel pressure element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseChannelPressureElement(String s) throws JFugueException {
        // A ChannelPressure token looks like this:
        //      +pressure
        //
        // where "pressure" can each be bytes or dictionary items

        String pressureString = s.substring(1, s.length());
        byte pressureNumber = getByteFromDictionary(pressureString);

        trace("ChannelPressure element: pressure = ", pressureNumber);
        fireChannelPressureEvent(pressureNumber);
    }

    class NoteContext {
        boolean isRest = false;
        boolean isNumericNote = false;
        boolean isChord = false;
        boolean isFirstNote = true;
        boolean isSequentialNote = false;
        boolean isParallelNote = false;
        boolean isNatural = false;
        boolean existAnotherNote = true;
        boolean anotherNoteIsSequential = false;
        boolean anotherNoteIsParallel = false;
        boolean isStartOfTie = false;
        boolean isEndOfTie = false;
        byte[] halfsteps = new byte[5];
        byte numHalfsteps = 0;
        byte noteNumber = 0;
        int octaveNumber = 0;
        double decimalDuration = 0.0;
        long duration = 0L;
        byte attackVelocity = Note.DEFAULT_VELOCITY;
        byte decayVelocity = Note.DEFAULT_VELOCITY;

        public NoteContext() {
            for (int i = 0; i < 5; i++) {
                halfsteps[i] = 0;
            }
        }
    }

    /**
     * Parses a note element.
     *
     * @param s the token that contains a note element
     * @throws JFugueException if there is a problem parsing the element
     */
    private void parseNoteElement(String s) throws JFugueException {
        NoteContext context = new NoteContext();

        while (context.existAnotherNote) {
            trace("--Parsing note from token " + s);
            decideSequentialOrParallel(context);
            int index = 0;
            int slen = s.length(); // We pass the length of the string because it is an invariant value that is used often
            index = parseNoteRoot(s, slen, index, context);
            index = parseNoteOctave(s, slen, index, context);
            index = parseNoteChord(s, slen, index, context);
            computeNoteValue(context);
            index = parseNoteChordInversion(s, slen, index, context);
            index = parseNoteDuration(s, slen, index, context);
            index = parseNoteVelocity(s, slen, index, context);
            s = parseNoteConnector(s, slen, index, context);
            fireNoteEvents(context);
        }
    }

    private void decideSequentialOrParallel(NoteContext context) {
        // Test whether this note is already known to be sequential (was connected with _) or parallel (was connected with +)
        context.isSequentialNote = false;
        if (context.anotherNoteIsSequential) {
            context.isSequentialNote = true;
            context.anotherNoteIsSequential = false;
            trace("This note is sequential");
        }

        context.isParallelNote = false;
        if (context.anotherNoteIsParallel) {
            context.isParallelNote = true;
            context.anotherNoteIsParallel = false;
            trace("This note is parallel");
        }
    }

    /**
     * Returns the index with which to start parsing the next part of the string, once this method is done with its part
     */
    private int parseNoteRoot(String s, int slen, int index, NoteContext context) {
        switch (s.charAt(index)) {
            case '[':
                return parseNumericNote(s, slen, index, context);
            case 'R':
                return parseRest(s, slen, index, context);
            default:
                return parseLetterNote(s, slen, index, context);
        }
    }

    /**
     * Returns the index with which to start parsing the next part of the string, once this method is done with its part
     */
    private int parseNumericNote(String s, int slen, int index, NoteContext context) {
        int indexOfEndBracket = s.indexOf(']', index);
        String stringInBrackets = s.substring(1, indexOfEndBracket);
        context.noteNumber = getByteFromDictionary(stringInBrackets);
        context.isNumericNote = true;

        trace("This note is a numeric note with value ", context.noteNumber);
        return indexOfEndBracket + 1;
    }

    /**
     * Returns the index with which to start parsing the next part of the string, once this method is done with its part
     */
    private int parseRest(String s, int slen, int index, NoteContext context) {
        context.isRest = true;

        trace("This note is a Rest");
        return index + 1;
    }

    /**
     * Returns the index with which to start parsing the next part of the string, once this method is done with its part
     */
    private int parseLetterNote(String s, int slen, int index, NoteContext context) {
        switch (s.charAt(index)) {
            case 'C':
                context.noteNumber = 0;
                break;
            case 'D':
                context.noteNumber = 2;
                break;
            case 'E':
                context.noteNumber = 4;
                break;
            case 'F':
                context.noteNumber = 5;
                break;
            case 'G':
                context.noteNumber = 7;
                break;
            case 'A':
                context.noteNumber = 9;
                break;
            case 'B':
                context.noteNumber = 11;
                break;
            default:
                throw new JFugueException(JFugueException.NOTE_EXC, s);
        }
        index++;

        // Check for #, b, or n (sharp, flat, or natural) modifier
        boolean checkForModifiers = true;
        while (checkForModifiers) {
            if (index < slen) {
                switch (s.charAt(index)) {
                    case '#':
                        index++;
                        context.noteNumber++;  /*if (context.noteNumber == 12) context.noteNumber = 0; */
                        break;
                    case 'B':
                        index++;
                        context.noteNumber--;  /*if (context.noteNumber == -1) context.noteNumber = 11;*/
                        break;
                    case 'N':
                        index++;
                        context.isNatural = true;
                        checkForModifiers = false;
                        break;
                    default:
                        checkForModifiers = false;
                        break;
                }
            } else {
                checkForModifiers = false;
            }
        }

        trace("Note number within an octave (C=0, B=11): ", context.noteNumber);
        return index;
    }

    /**
     * Returns the index with which to start parsing the next part of the string, once this method is done with its part
     */
    private int parseNoteOctave(String s, int slen, int index, NoteContext context) {
        // Don't parse an octave for a rest or a numeric note
        if (context.isRest || context.isNumericNote) {
            return index;
        }

        // Check for octave.  Remember that octaves are optional.
        char possibleOctave1 = '.';
        char possibleOctave2 = '.';

        if (index < slen) {
            possibleOctave1 = s.charAt(index);
        }

        if (index + 1 < slen) {
            possibleOctave2 = s.charAt(index + 1);
        }

        byte definiteOctaveLength = 0;
        if ((possibleOctave1 >= '0') && (possibleOctave1 <= '9')) {
            definiteOctaveLength = 1;
            if ((possibleOctave2 >= '0') && (possibleOctave2 <= '9')) {
                definiteOctaveLength = 2;
            }

            String octaveNumberString = s.substring(index, index + definiteOctaveLength);
            try {
                context.octaveNumber = Byte.parseByte(octaveNumberString);
            } catch (NumberFormatException e) {
                throw new JFugueException(JFugueException.OCTAVE_EXC, octaveNumberString, s);
            }
            if (context.octaveNumber > 10) {
                throw new JFugueException(JFugueException.OCTAVE_EXC, octaveNumberString, s);
            }
        }

        return index + definiteOctaveLength;
    }

    /**
     * Returns the index with which to start parsing the next part of the string, once this method is done with its part
     */
    private int parseNoteChord(String s, int slen, int index, NoteContext context) {
        // Don't parse chord for a rest 
        if (context.isRest) {
            return index;
        }

        String possibleChord3 = null;
        String possibleChord4 = null;
        String possibleChord5 = null;
        String possibleChord6 = null;
        String possibleChord7 = null;
        String possibleChord8 = null;
        try {
            possibleChord3 = s.substring(index, index + 3);
            possibleChord4 = s.substring(index, index + 4);
            possibleChord5 = s.substring(index, index + 5);
            possibleChord6 = s.substring(index, index + 6);
            possibleChord7 = s.substring(index, index + 7);
            possibleChord8 = s.substring(index, index + 8);
        } catch (IndexOutOfBoundsException e) {
            // Nothing to do... just needed to catch
        }

        int lengthOfChordString = 0;  // This represents the length of the string, not the number of halfsteps

        // Below, 'chordLength' refers to the size of the text for the chord (for example, "min"=3, "dim7"=4),
        // and 'numHalfsteps' refers to the number of elements in the halfsteps array.
        // This must be done in order from smaller to larger strings, so the longer string names
        // take effect.  This means 'min' can be overwritten by 'minmaj7', or 'maj' by 'maj7', for example.

        if (possibleChord3 != null) {
            if (possibleChord3.equals("MAJ")) {
                lengthOfChordString = 3;
                context.numHalfsteps = 2;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 7;
            } else if (possibleChord3.equals("MIN")) {
                lengthOfChordString = 3;
                context.numHalfsteps = 2;
                context.halfsteps[0] = 3;
                context.halfsteps[1] = 7;
            } else if (possibleChord3.equals("AUG")) {
                lengthOfChordString = 3;
                context.numHalfsteps = 2;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 8;
            } else if (possibleChord3.equals("DIM")) {
                lengthOfChordString = 3;
                context.numHalfsteps = 2;
                context.halfsteps[0] = 3;
                context.halfsteps[1] = 6;
            }
        }
        if (possibleChord4 != null) {
            if (possibleChord4.equalsIgnoreCase("DOM7")) {
                lengthOfChordString = 4;
                context.numHalfsteps = 3;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 7;
                context.halfsteps[2] = 10;
            } else if (possibleChord4.equalsIgnoreCase("MAJ7")) {
                lengthOfChordString = 4;
                context.numHalfsteps = 3;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 7;
                context.halfsteps[2] = 11;
            } else if (possibleChord4.equalsIgnoreCase("MIN7")) {
                lengthOfChordString = 4;
                context.numHalfsteps = 3;
                context.halfsteps[0] = 3;
                context.halfsteps[1] = 7;
                context.halfsteps[2] = 10;
            } else if (possibleChord4.equalsIgnoreCase("SUS4")) {
                lengthOfChordString = 4;
                context.numHalfsteps = 2;
                context.halfsteps[0] = 5;
                context.halfsteps[1] = 7;
            } else if (possibleChord4.equalsIgnoreCase("SUS2")) {
                lengthOfChordString = 4;
                context.numHalfsteps = 2;
                context.halfsteps[0] = 2;
                context.halfsteps[1] = 7;
            } else if (possibleChord4.equalsIgnoreCase("MAJ6")) {
                lengthOfChordString = 4;
                context.numHalfsteps = 3;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 7;
                context.halfsteps[2] = 9;
            } else if (possibleChord4.equalsIgnoreCase("MIN6")) {
                lengthOfChordString = 4;
                context.numHalfsteps = 3;
                context.halfsteps[0] = 3;
                context.halfsteps[1] = 7;
                context.halfsteps[2] = 9;
            } else if (possibleChord4.equalsIgnoreCase("DOM9")) {
                lengthOfChordString = 4;
                context.numHalfsteps = 4;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 7;
                context.halfsteps[2] = 10;
                context.halfsteps[3] = 14;
            } else if (possibleChord4.equalsIgnoreCase("MAJ9")) {
                lengthOfChordString = 4;
                context.numHalfsteps = 4;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 7;
                context.halfsteps[2] = 11;
                context.halfsteps[3] = 14;
            } else if (possibleChord4.equalsIgnoreCase("MIN9")) {
                lengthOfChordString = 4;
                context.numHalfsteps = 4;
                context.halfsteps[0] = 3;
                context.halfsteps[1] = 7;
                context.halfsteps[2] = 10;
                context.halfsteps[3] = 14;
            } else if (possibleChord4.equalsIgnoreCase("DIM7")) {
                lengthOfChordString = 4;
                context.numHalfsteps = 3;
                context.halfsteps[0] = 3;
                context.halfsteps[1] = 6;
                context.halfsteps[2] = 9;
            } else if (possibleChord4.equalsIgnoreCase("ADD9")) {
                lengthOfChordString = 4;
                context.numHalfsteps = 3;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 7;
                context.halfsteps[2] = 14;
            } else if (possibleChord4.equalsIgnoreCase("DAVE")) {
                lengthOfChordString = 4;
                context.numHalfsteps = 3;
                context.halfsteps[0] = 7;
                context.halfsteps[1] = 14;
                context.halfsteps[2] = 21;
            }
        }

        if (possibleChord5 != null) {
            if (possibleChord5.equalsIgnoreCase("MIN11")) {
                lengthOfChordString = 5;
                context.numHalfsteps = 5;
                context.halfsteps[0] = 7;
                context.halfsteps[1] = 10;
                context.halfsteps[2] = 14;
                context.halfsteps[3] = 15;
                context.halfsteps[4] = 17;
            } else if (possibleChord5.equalsIgnoreCase("DOM11")) {
                lengthOfChordString = 5;
                context.numHalfsteps = 4;
                context.halfsteps[0] = 7;
                context.halfsteps[1] = 10;
                context.halfsteps[2] = 14;
                context.halfsteps[3] = 17;
            } else if (possibleChord5.equalsIgnoreCase("DOM13")) {
                lengthOfChordString = 5;
                context.numHalfsteps = 5;
                context.halfsteps[0] = 7;
                context.halfsteps[1] = 10;
                context.halfsteps[2] = 14;
                context.halfsteps[3] = 16;
                context.halfsteps[4] = 21;
            } else if (possibleChord5.equalsIgnoreCase("MIN13")) {
                lengthOfChordString = 5;
                context.numHalfsteps = 5;
                context.halfsteps[0] = 7;
                context.halfsteps[1] = 10;
                context.halfsteps[2] = 14;
                context.halfsteps[3] = 15;
                context.halfsteps[4] = 21;
            } else if (possibleChord5.equalsIgnoreCase("MAJ13")) {
                lengthOfChordString = 5;
                context.numHalfsteps = 5;
                context.halfsteps[0] = 7;
                context.halfsteps[1] = 11;
                context.halfsteps[2] = 14;
                context.halfsteps[3] = 16;
                context.halfsteps[4] = 21;
            }
        }

        if (possibleChord6 != null) {
            if (possibleChord6.equalsIgnoreCase("DOM7<5")) {
                lengthOfChordString = 6;
                context.numHalfsteps = 3;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 6;
                context.halfsteps[2] = 10;
            } else if (possibleChord6.equalsIgnoreCase("DOM7>5")) {
                lengthOfChordString = 6;
                context.numHalfsteps = 3;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 8;
                context.halfsteps[2] = 10;
            } else if (possibleChord6.equalsIgnoreCase("MAJ7<5")) {
                lengthOfChordString = 6;
                context.numHalfsteps = 3;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 6;
                context.halfsteps[2] = 11;
            } else if (possibleChord6.equalsIgnoreCase("MAJ7>5")) {
                lengthOfChordString = 6;
                context.numHalfsteps = 3;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 8;
                context.halfsteps[2] = 11;
            }
        }

        if (possibleChord7 != null) {
            if (possibleChord7.equalsIgnoreCase("minmaj7")) {
                lengthOfChordString = 7;
                context.numHalfsteps = 3;
                context.halfsteps[0] = 3;
                context.halfsteps[1] = 7;
                context.halfsteps[2] = 11;
            }
        }

        if (possibleChord8 != null) {
            if (possibleChord8.equalsIgnoreCase("DOM7<5<9")) {
                lengthOfChordString = 8;
                context.numHalfsteps = 4;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 6;
                context.halfsteps[2] = 10;
                context.halfsteps[3] = 13;
            } else if (possibleChord8.equalsIgnoreCase("DOM7<5>9")) {
                lengthOfChordString = 8;
                context.numHalfsteps = 4;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 6;
                context.halfsteps[2] = 10;
                context.halfsteps[3] = 15;
            } else if (possibleChord8.equalsIgnoreCase("DOM7>5<9")) {
                lengthOfChordString = 8;
                context.numHalfsteps = 4;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 8;
                context.halfsteps[2] = 10;
                context.halfsteps[3] = 13;
            } else if (possibleChord8.equalsIgnoreCase("DOM7>5>9")) {
                lengthOfChordString = 8;
                context.numHalfsteps = 4;
                context.halfsteps[0] = 4;
                context.halfsteps[1] = 8;
                context.halfsteps[2] = 10;
                context.halfsteps[3] = 15;
            }
        }

        if (lengthOfChordString > 0) {
            context.isChord = true;
            trace("Chord: chordLength=", lengthOfChordString, ", so chord is one of the following: [ 3=", possibleChord3, " 4=", possibleChord4, " 5=", possibleChord5, " 6=", possibleChord6, " 7=", possibleChord7, " 8=", possibleChord8, " ]");
        }

        return index + lengthOfChordString;
    }

    /**
     * This method does a variety of calculations to get the actual value of the note.
     */
    private void computeNoteValue(NoteContext context) {
        // Don't compute note value for a rest 
        if (context.isRest) {
            return;
        }

        // If we happen not to have an octave yet, set it to a default value.
        // Default octave: 5 for notes, 3 for chords
        if ((context.octaveNumber == 0) && (!context.isNumericNote)) {
            if (context.isChord) {
                context.octaveNumber = 3;
            } else {
                context.octaveNumber = 5;
            }
        }
        trace("Octave: ", context.octaveNumber);

        // Adjust for Key Signature
        if ((keySig != 0) && (!context.isNatural)) {
            if ((keySig <= -1) && (context.noteNumber == 11)) context.noteNumber = 10;
            if ((keySig <= -2) && (context.noteNumber == 4)) context.noteNumber = 3;
            if ((keySig <= -3) && (context.noteNumber == 9)) context.noteNumber = 8;
            if ((keySig <= -4) && (context.noteNumber == 2)) context.noteNumber = 1;
            if ((keySig <= -5) && (context.noteNumber == 7)) context.noteNumber = 6;
            if ((keySig <= -6) && (context.noteNumber == 0)) {
                context.noteNumber = 11;
                context.octaveNumber--;
            }
            if ((keySig <= -7) && (context.noteNumber == 5)) context.noteNumber = 4;
            if ((keySig >= +1) && (context.noteNumber == 5)) context.noteNumber = 6;
            if ((keySig >= +2) && (context.noteNumber == 0)) context.noteNumber = 1;
            if ((keySig >= +3) && (context.noteNumber == 7)) context.noteNumber = 8;
            if ((keySig >= +4) && (context.noteNumber == 2)) context.noteNumber = 3;
            if ((keySig >= +5) && (context.noteNumber == 9)) context.noteNumber = 10;
            if ((keySig >= +6) && (context.noteNumber == 4)) context.noteNumber = 5;
            if ((keySig >= +7) && (context.noteNumber == 11)) {
                context.noteNumber = 0;
                context.octaveNumber++;
            }
            trace("After adjusting for Key Signature, noteNumber=", context.noteNumber, " octave=", context.octaveNumber);
        }

        // Compute the actual note number, based on octave and note
        if (!context.isNumericNote) {
            int intNoteNumber = (context.octaveNumber * 12) + context.noteNumber;
            if (intNoteNumber > 127) {
                throw new JFugueException(JFugueException.NOTE_OCTAVE_EXC, Integer.toString(intNoteNumber), "");
            }
            context.noteNumber = (byte) intNoteNumber;
            trace("Computed note number: ", context.noteNumber);
        }
    }

    /**
     * Returns the index with which to start parsing the next part of the string, once this method is done with its part
     */
    private int parseNoteChordInversion(String s, int slen, int index, NoteContext context) {
        if (!context.isChord) {
            return index;
        }

        int inversionCount = 0;
        int inversionRootNote = -1;
        int inversionOctave = -1;

        boolean checkForInversion = true;
        while (checkForInversion) {
            if (index < slen) {
                switch (s.charAt(index)) {
                    case '^':
                        index++;
                        inversionCount++;
                        break;
                    case 'C':
                        index++;
                        inversionRootNote = 0;
                        break;
                    case 'D':
                        index++;
                        inversionRootNote = 2;
                        break;
                    case 'E':
                        index++;
                        inversionRootNote = 4;
                        break;
                    case 'F':
                        index++;
                        inversionRootNote = 5;
                        break;
                    case 'G':
                        index++;
                        inversionRootNote = 7;
                        break;
                    case 'A':
                        index++;
                        inversionRootNote = 9;
                        break;
                    // For 'B', need to differentiate between B note and 'b' flat
                    case 'B':
                        index++;
                        if (inversionRootNote == -1) {
                            inversionRootNote = 11;
                        } else {
                            inversionRootNote--;
                        }
                        break;
                    case '#':
                        index++;
                        inversionRootNote++;
                        break;
                    // For '0', need to differentiate between initial 0 and 0 as a second digit (i.e., 10)
                    case '0':
                        index++;
                        if (inversionOctave == -1) {
                            inversionOctave = 0;
                        } else {
                            inversionOctave = inversionOctave * 10;
                        }
                        break;
                    case '1':
                        index++;
                        inversionOctave = 1;
                        break;
                    case '2':
                        index++;
                        inversionOctave = 2;
                        break;
                    case '3':
                        index++;
                        inversionOctave = 3;
                        break;
                    case '4':
                        index++;
                        inversionOctave = 4;
                        break;
                    case '5':
                        index++;
                        inversionOctave = 5;
                        break;
                    case '6':
                        index++;
                        inversionOctave = 6;
                        break;
                    case '7':
                        index++;
                        inversionOctave = 7;
                        break;
                    case '8':
                        index++;
                        inversionOctave = 8;
                        break;
                    case '9':
                        index++;
                        inversionOctave = 9;
                        break;
                    // If [, whoo boy, we're checking for a note number
                    case '[':
                        int indexEndBracket = s.indexOf(']', index);
                        inversionRootNote = Integer.parseInt(s.substring(index + 1, indexEndBracket - 1));
                        index = indexEndBracket + 1;
                        break;
                    default:
                        checkForInversion = false;
                        break;
                }
            } else {
                checkForInversion = false;
            }
        }

        // Modify the note values based on the inversion
        if (inversionCount > 0) {
            if (inversionRootNote == -1) {
                // The root is determined by a number of carets.  Increase each half-step
                // before the inversion by 12, the number of notes in an octave.
                trace("Inversion is base on count: " + inversionCount);
                trace("Inverting " + context.noteNumber + " to be " + (context.noteNumber + 12));
                context.noteNumber += 12;
                for (int i = inversionCount - 1; i < context.numHalfsteps; i++) {
                    trace("Inverting " + context.halfsteps[i] + " to be " + (context.halfsteps[i] - 12));
                    context.halfsteps[i] -= 12;
                }
            } else {
                // The root is determined by an inversionRoot.  This is much trickier, but we can
                // still figure it out.
                if (inversionOctave != -1) {
                    inversionRootNote += inversionOctave * 12;
                } else if (inversionRootNote < 12) {
                    int currentOctave = context.noteNumber / 12;
                    inversionRootNote += currentOctave * 12;
                }
                // Otherwise, inversionRootNote is a numeric note value, like [60]

                trace("Inversion is base on note: " + inversionRootNote);

                if ((inversionRootNote > context.noteNumber + context.halfsteps[context.numHalfsteps - 1]) || (inversionRootNote < context.noteNumber)) {
                    throw new JFugueException(JFugueException.INVERSION_EXC);
                }

                trace("Inverting " + context.noteNumber + " to be " + (context.noteNumber + 12));
                context.noteNumber += 12;
                for (int i = 0; i < context.numHalfsteps; i++) {
                    if (context.noteNumber + context.halfsteps[i] >= inversionRootNote + 12) {
                        trace("Inverting " + context.halfsteps[i] + " to be " + (context.halfsteps[i] - 12));
                        context.halfsteps[i] -= 12;
                    }
                }
            }
        }

        return index;
    }

    /**
     * Returns the index with which to start parsing the next part of the string, once this method is done with its part
     */
    private int parseNoteDuration(String s, int slen, int index, NoteContext context) {
        context.decimalDuration = 0.0;
        if (index < slen) {
            switch (s.charAt(index)) {
                case '/':
                    index = parseNumericDuration(s, slen, index, context);
                    break;
                case 'W':
                case 'H':
                case 'Q':
                case 'I':
                case 'S':
                case 'T':
                case 'X':
                case 'O':
                case '-':
                    index = parseLetterDuration(s, slen, index, context);
                    break;
                default:
                    break;
            }
            index = parseTuplet(s, slen, index, context);
        } else {
            context.decimalDuration = 1.0 / 4.0; // Default duration is a quarter note
        }

//        context.duration = (long) (120.0 * 4.0 * context.decimalDuration); // javax.sound.midi.Sequence resolution is 120
        context.duration = (long) (120.0 * context.decimalDuration); // DMK 9/27/08: The *4.0 makes quarter notes 4 times as long as they should be

//        // Below is incorrect, as identified by M. Ahluwalia
//        // Tempo is now in Beats Per Minute.  Convert this to Pulses Per Quarter (PPQ), then to
//        // Pulses Per Whole (PPW), then multiply that by durationNumber for WHQITXN notes
//        double ppq = 60000000.0D / (double)this.getTempo();
//        double ppw = ppq * 4.0; // 4 quarter notes in a whole note
//        context.duration = (long)(ppw * context.decimalDuration) / 4000; 

        trace("Decimal duration is ", context.decimalDuration);
        trace("Actual duration is ", context.duration);

        return index;
    }

    /**
     * Returns the index with which to start parsing the next part of the string, once this method is done with its part
     */
    private int parseLetterDuration(String s, int slen, int index, NoteContext context) {
        // Check duration
        boolean durationExists = true;
        boolean isDotted = false;

        while (durationExists) {
            int durationNumber = 0;
            // See if the note has a duration
            // Duration is optional; default is Q (4)
            if (index < slen) {
                char durationChar = s.charAt(index);
                switch (durationChar) {
                    case '-':
                        if ((context.decimalDuration == 0) && (!context.isEndOfTie)) {
                            context.isEndOfTie = true;
                            trace("Note is end of tie");
                        } else {
                            context.isStartOfTie = true;
                            trace("Note is start of tie");
                        }
                        break;
                    case 'W':
                        durationNumber = 1;
                        break;
                    case 'H':
                        durationNumber = 2;
                        break;
                    case 'Q':
                        durationNumber = 4;
                        break;
                    case 'I':
                        durationNumber = 8;
                        break;
                    case 'S':
                        durationNumber = 16;
                        break;
                    case 'T':
                        durationNumber = 32;
                        break;
                    case 'X':
                        durationNumber = 64;
                        break;
                    case 'O':
                        durationNumber = 128;
                        break;
                    default:
                        index--;
                        durationExists = false;
                        break;
                }
                index++;
                if ((index < slen) && (s.charAt(index) == '.')) {
                    isDotted = true;
                    index++;
                }

                if (durationNumber > 0) {
                    double d = 1.0 / durationNumber;
                    if (isDotted) {
                        context.decimalDuration += d + (d / 2.0);
                    } else {
                        context.decimalDuration += d;
                    }
                }
            } else {
                durationExists = false;
            }
        }

        return index;
    }

    /**
     * Returns the index with which to start parsing the next part of the string, once this method is done with its part
     */
    private int parseNumericDuration(String s, int slen, int index, NoteContext context) {
        // The duration has come in as a number, like 0.25 for a quarter note.
        // Advance pointer past the initial slash (/)
        index++;

        // Decimal duration is not required to be enclosed by brackets,
        // but since most of the other numerical input to a MusicString
        // is required to be in brackets, we should support it.
        if ('[' == s.charAt(index)) {
            int indexOfEndingBracket = s.indexOf(']', index);
            context.decimalDuration += getDoubleFromDictionary(s.substring(index + 1, indexOfEndingBracket));
            index = indexOfEndingBracket + 1;
        } else {
            int endingIndex = index;
            boolean keepAdvancingPointer = true;
            while (keepAdvancingPointer) {
                try {
                    char numericDurationChar = s.charAt(endingIndex);
                    if ((numericDurationChar >= '0') && (numericDurationChar <= '9') || (numericDurationChar == '.'))  // Decimal dot, not dotted duration
                    {
                        endingIndex++;
                    } else {
                        keepAdvancingPointer = false;
                    }
                } catch (IndexOutOfBoundsException e) {
                    keepAdvancingPointer = false;
                }
            }
            String durationNumberString = s.substring(index, endingIndex);
            context.decimalDuration += Double.parseDouble(durationNumberString);
            index = endingIndex;
        }

        trace("Decimal duration is ", context.decimalDuration);
        return index;
    }

    /**
     * Returns the index with which to start parsing the next part of the string, once this method is done with its part
     */
    private int parseTuplet(String s, int slen, int index, NoteContext context) {
        if (index < slen) {
            if (s.charAt(index) == '*') {
                trace("Note is a tuplet");
                index++;

                // Figure out tuplet ratio, or figure out when to stop looking for tuplet info
                boolean stopTupletParsing = false;
                int indexOfUnitsToMatch = 0;
                int indexOfNumNotes = 0;
                int counter = -1;
                while (!stopTupletParsing) {
                    counter++;
                    if (slen > index + counter) {
                        if (s.charAt(index + counter) == ':') {
                            indexOfNumNotes = index + counter + 1;
                        } else if ((s.charAt(index + counter) >= '0') && (s.charAt(index + counter) <= '9')) {
                            if (indexOfUnitsToMatch == 0) {
                                indexOfUnitsToMatch = index + counter;
                            }
                        } else if ((s.charAt(index + counter) == '*')) {
                            // no op... artifact of parsing
                        } else {
                            stopTupletParsing = true;
                        }
                    } else {
                        stopTupletParsing = true;
                    }
                }

                index += counter;

                double numerator = 2.0;
                double denominator = 3.0;
                if ((indexOfUnitsToMatch > 0) && (indexOfNumNotes > 0)) {
                    numerator = Double.parseDouble(s.substring(indexOfUnitsToMatch, indexOfNumNotes - 1));
                    denominator = Double.parseDouble(s.substring(indexOfNumNotes, index));
                }
                trace("Tuplet ratio is " + numerator + ':' + denominator);
                double tupletRatio = numerator / denominator;
                context.decimalDuration = context.decimalDuration * tupletRatio;
                trace("Decimal duration after tuplet is ", context.decimalDuration);
            }
        }

        return index;
    }

    /**
     * Returns the index with which to start parsing the next part of the string, once this method is done with its part
     */
    private int parseNoteVelocity(String s, int slen, int index, NoteContext context) {
        // Don't compute note velocity for a rest 
        if (context.isRest) {
            return index;
        }

        // Process velocity attributes, if they exist
        while (index < slen) {
            int startPoint = index + 1;
            int endPoint = startPoint;

            char velocityChar = s.charAt(index);
            int lengthOfByte = 0;
            if ((velocityChar == '+') || (velocityChar == '_')) break;
            trace("Identified Velocity character ", velocityChar);
            boolean byteDone = false;
            while (!byteDone && (index + lengthOfByte + 1 < slen)) {
                char possibleByteChar = s.charAt(index + lengthOfByte + 1);
                if ((possibleByteChar >= '0') && (possibleByteChar <= '9')) {
                    lengthOfByte++;
                } else {
                    byteDone = true;
                }
            }
            endPoint = index + lengthOfByte + 1;

            // Or maybe a bracketed string was passed in, instead of a byte
            if ((index + 1 < slen) && (s.charAt(index + 1) == '[')) {
                endPoint = s.indexOf(']', startPoint) + 1;
            }

            byte velocityNumber = getByteFromDictionary(s.substring(startPoint, endPoint));

            switch (velocityChar) {
                case 'A':
                    context.attackVelocity = velocityNumber;
                    break;
                case 'D':
                    context.decayVelocity = velocityNumber;
                    break;
                default:
                    throw new JFugueException(JFugueException.NOTE_VELOCITY_EXC, s.substring(startPoint, endPoint), s);
            }
            index = endPoint;
        }
        trace("Attack velocity = ", context.attackVelocity, "; Decay velocity = ", context.decayVelocity);
        return index;
    }

    /**
     * Returns the String of the next sub-token (the parts after + or _), if one exists; otherwise, returns null
     */
    private String parseNoteConnector(String s, int slen, int index, NoteContext context) {
        context.existAnotherNote = false;
        // See if there's another note to process
        if ((index < slen) && ((s.charAt(index) == '+') || (s.charAt(index) == '_'))) {
            trace("Another note: string = ", s.substring(index, s.length() - 1));
            if (s.charAt(index) == '_') {
                context.anotherNoteIsSequential = true;
                trace("Next note will be sequential");
            } else {
                context.anotherNoteIsParallel = true;
                trace("Next note will be parallel");
            }
            index++;
            context.existAnotherNote = true;
            return s.substring(index, slen);
        }
        return null;
    }

    private void fireNoteEvents(NoteContext context) {
        // Set up the note
        Note note = new Note();

        if (context.isRest) {
            note.setRest(true);
            note.setDuration(context.duration);
            note.setDecimalDuration(context.decimalDuration);
            note.setAttackVelocity((byte) 0);          // turn off sound for rest notes
            note.setDecayVelocity((byte) 0);
        } else {
            note.setValue(context.noteNumber);
            note.setDuration(context.duration);
            note.setStartOfTie(context.isStartOfTie);
            note.setEndOfTie(context.isEndOfTie);
            note.setDecimalDuration(context.decimalDuration);
            note.setAttackVelocity(context.attackVelocity);
            note.setDecayVelocity(context.decayVelocity);
        }
        note.setHasAccompanyingNotes(context.existAnotherNote || context.isChord);

        // Fire note events
        if (context.isFirstNote) {
            note.setType(Note.FIRST);
            trace("Firing first note event");
            fireNoteEvent(note);
        } else if (context.isSequentialNote) {
            note.setType(Note.SEQUENTIAL);
            trace("Firing sequential note event");
            fireSequentialNoteEvent(note);
        } else if (context.isParallelNote) {
            note.setType(Note.PARALLEL);
            trace("Firing parallel note event");
            fireParallelNoteEvent(note);
        }

        if (context.isChord) {
            for (int i = 0; i < context.numHalfsteps; i++) {
                Note chordNote = new Note((byte) (context.noteNumber + context.halfsteps[i]), context.duration);
                chordNote.setDecimalDuration(context.decimalDuration); // This won't have any effect on the note, but it's good bookkeeping to have it around.
                chordNote.setType(Note.PARALLEL);
                trace("Chord note number: ", (context.noteNumber + context.halfsteps[i]));
                if (i == context.numHalfsteps - 1) {
                    chordNote.setHasAccompanyingNotes(context.existAnotherNote);
                } else {
                    chordNote.setHasAccompanyingNotes(context.existAnotherNote || context.isChord);
                }
                fireParallelNoteEvent(chordNote);
            }
        }
        context.isFirstNote = false;
    }

    /**
     * Looks up a string's value in the dictionary.  The dictionary is used to
     * keep memorable names of obscure numbers - for example, the string FLUTE
     * is set to a value of 73, so when users want to play music with a flute,
     * they can say "I[Flute]" instead of "I[73]".
     * <p/>
     * <p>
     * The Dictionary feature also lets users define constants so that if the
     * value of something were to change, it only needs to be changed in one
     * place.  For example, MY_FAVORITE_INSTRUMENT could be set to 73, then you
     * can say "I[My_Favorite_Instrument]" when you want to play with that
     * instrument.  If your favorite instrument were ever to change, you only
     * have to make the change in one place, instead of every place where you
     * give the Instrument command.
     * </p>
     *
     * @param bracketedString the string to look up in the dictionary
     * @throws JFugueException if there is a problem looking up bracketedString
     * @returns the definition of the string
     */
    private String dictionaryLookup(String bracketedString) throws JFugueException {
        int indexOfOpeningBracket = bracketedString.indexOf('[');
        int indexOfClosingBracket = bracketedString.indexOf(']');

        String word = null;
        if ((indexOfOpeningBracket != -1) && (indexOfClosingBracket != -1)) {
            word = bracketedString.substring(indexOfOpeningBracket + 1, indexOfClosingBracket);
        } else {
            // It appears that "bracketedString" wasn't bracketed.
            word = bracketedString;
        }
        word = word.toUpperCase();

        String definition = (String) dictionaryMap.get(word);
        while ((definition != null) && (dictionaryMap.containsKey(definition.toUpperCase()))) {
            definition = (String) dictionaryMap.get(definition.toUpperCase());
        }

        // If there is no definition for this word, see if the word is actually a number.
        if (null == definition) {
            char ch = 0;
            boolean isNumber = true;
            for (int i = 0; i < word.length(); i++) {
                ch = word.charAt(i);
                if ((!Character.isDigit(ch) && (ch != '.'))) {
                    isNumber = false;
                }
            }
            if (isNumber) {
                trace("Dictionary lookup returning the number ", word);
                return word;
            } else {
                throw new JFugueException(JFugueException.WORD_NOT_DEFINED_EXC, word, bracketedString);
            }
        }
        trace("Word ", word, " is defined as ", definition);
        return definition;
    }

    /**
     * Look up a byte from the dictionary
     *
     * @param bracketedString the string to look up
     * @throws JFugueException if there is a problem getting a byte from the dictionary look-up
     * @returns the byte value of the definition
     */
    private byte getByteFromDictionary(String bracketedString) throws JFugueException {
        String definition = dictionaryLookup(bracketedString);
        Byte newbyte = null;
        try {
            newbyte = new Byte(definition);
        } catch (NumberFormatException e) {
            throw new JFugueException(JFugueException.EXPECTED_BYTE, definition, bracketedString);
        }
        return newbyte.byteValue();
    }

    /**
     * Look up an int from the dictionary
     *
     * @param bracketedString the string to look up
     * @throws JFugueException if there is a problem getting a int from the dictionary look-up
     * @returns the int value of the definition
     */
    private int getIntFromDictionary(String bracketedString) throws JFugueException {
        String definition = dictionaryLookup(bracketedString);
        Integer newint = null;
        try {
            newint = new Integer(definition);
        } catch (NumberFormatException e) {
            throw new JFugueException(JFugueException.EXPECTED_INT, definition, bracketedString);
        }
        return newint.intValue();
    }

    /**
     * Look up a double from the dictionary
     *
     * @param bracketedString the string to look up
     * @throws JFugueException if there is a problem getting a double from the dictionary look-up
     * @returns the double value of the definition
     */
    private double getDoubleFromDictionary(String bracketedString) throws JFugueException {
        String definition = dictionaryLookup(bracketedString);
        Double newdouble = null;
        try {
            newdouble = new Double(definition);
        } catch (NumberFormatException e) {
            throw new JFugueException(JFugueException.EXPECTED_DOUBLE, definition, bracketedString);
        }
        return newdouble.doubleValue();
    }

    /**
     * Parses a string which presumably contains one token, which is a note.
     *
     * @param string The String that contains one token with a note, like "C5"
     * @return a Note object representing the note parsed from the string
     */
    public static Note getNote(String string) {
        return getNote(new Pattern(string));
    }

    /**
     * Parses a pattern which presumably contains one token, which is a note.
     *
     * @param pattern The Pattern that contains one token with a note, like "C5"
     * @return a Note object representing the note parsed from the pattern
     */
    public static Note getNote(Pattern pattern) {
        final Note rootNote = new Note();

        MusicStringParser parser = new MusicStringParser();
        ParserListener renderer = new ParserListenerAdapter() {
            public void noteEvent(Note note) {
                rootNote.setValue(note.getValue());
                rootNote.setDuration(note.getDuration());
                rootNote.setDecimalDuration(note.getDecimalDuration());
                rootNote.setStartOfTie(note.isStartOfTie());
                rootNote.setEndOfTie(note.isEndOfTie());
                rootNote.setAttackVelocity(note.getAttackVelocity());
                rootNote.setDecayVelocity(note.getDecayVelocity());
                rootNote.setRest(note.isRest());
            }
        };

        parser.addParserListener(renderer);
        parser.parse(pattern);

        return rootNote;
    }
}
