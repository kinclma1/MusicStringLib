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

import java.util.Map;

/**
 * Loads default definitions into the JFugue dictionary.
 * This allows users to refer to instruments, percussion sounds,
 * and controller events by easy-to-remember names.
 * <p/>
 * <p>
 * Dictionary items can be added via the Music String.  See
 * the documentation for more information.
 * </p>
 *
 * @author David Koelle
 * @version 2.0
 */
public final class JFugueDefinitions {
    private JFugueDefinitions() {
    }

    /**
     * Loads default definitions into the JFugue dictionary.  This includes
     * all of the string representations for instrument names, percussion sounds,
     * controller events, and some controller values.
     *
     * @param dictionaryMap the dictionary instantiated by the parser
     */
    protected static void populateDictionary(Map<String, Object> dictionaryMap) {
        //
        // Instrument names
        //
        dictionaryMap.put("PIANO", "0");
        dictionaryMap.put("ACOUSTIC_GRAND", "0");
        dictionaryMap.put("BRIGHT_ACOUSTIC", "1");
        dictionaryMap.put("ELECTRIC_GRAND", "2");
        dictionaryMap.put("HONKEY_TONK", "3");
        dictionaryMap.put("ELECTRIC_PIANO", "4");
        dictionaryMap.put("ELECTRIC_PIANO_1", "4");
        dictionaryMap.put("ELECTRIC_PIANO_2", "5");
        dictionaryMap.put("HARPISCHORD", "6");
        dictionaryMap.put("CLAVINET", "7");
        dictionaryMap.put("CELESTA", "8");
        dictionaryMap.put("GLOCKENSPIEL", "9");

        dictionaryMap.put("MUSIC_BOX", "10");
        dictionaryMap.put("VIBRAPHONE", "11");
        dictionaryMap.put("MARIMBA", "12");
        dictionaryMap.put("XYLOPHONE", "13");
        dictionaryMap.put("TUBULAR_BELLS", "14");
        dictionaryMap.put("DULCIMER", "15");
        dictionaryMap.put("DRAWBAR_ORGAN", "16");
        dictionaryMap.put("PERCUSSIVE_ORGAN", "17");
        dictionaryMap.put("ROCK_ORGAN", "18");
        dictionaryMap.put("CHURCH_ORGAN", "19");

        dictionaryMap.put("REED_ORGAN", "20");
        dictionaryMap.put("ACCORDIAN", "21");
        dictionaryMap.put("HARMONICA", "22");
        dictionaryMap.put("TANGO_ACCORDIAN", "23");
        dictionaryMap.put("GUITAR", "24");
        dictionaryMap.put("NYLON_STRING_GUITAR", "24");
        dictionaryMap.put("STEEL_STRING_GUITAR", "25");
        dictionaryMap.put("ELECTRIC_JAZZ_GUITAR", "26");
        dictionaryMap.put("ELECTRIC_CLEAN_GUITAR", "27");
        dictionaryMap.put("ELECTRIC_MUTED_GUITAR", "28");
        dictionaryMap.put("OVERDRIVEN_GUITAR", "29");

        dictionaryMap.put("DISTORTION_GUITAR", "30");
        dictionaryMap.put("GUITAR_HARMONICS", "31");
        dictionaryMap.put("ACOUSTIC_BASS", "32");
        dictionaryMap.put("ELECTRIC_BASS_FINGER", "33");
        dictionaryMap.put("ELECTRIC_BASS_PICK", "34");
        dictionaryMap.put("FRETLESS_BASS", "35");
        dictionaryMap.put("SLAP_BASS_1", "36");
        dictionaryMap.put("SLAP_BASS_2", "37");
        dictionaryMap.put("SYNTH_BASS_1", "38");
        dictionaryMap.put("SYNTH_BASS_2", "39");

        dictionaryMap.put("VIOLIN", "40");
        dictionaryMap.put("VIOLA", "41");
        dictionaryMap.put("CELLO", "42");
        dictionaryMap.put("CONTRABASS", "43");
        dictionaryMap.put("TREMOLO_STRINGS", "44");
        dictionaryMap.put("PIZZICATO_STRINGS", "45");
        dictionaryMap.put("ORCHESTRAL_STRINGS", "46");
        dictionaryMap.put("TIMPANI", "47");
        dictionaryMap.put("STRING_ENSEMBLE_1", "48");
        dictionaryMap.put("STRING_ENSEMBLE_2", "49");

        dictionaryMap.put("SYNTH_STRINGS_1", "50");
        dictionaryMap.put("SYNTH_STRINGS_2", "51");
        dictionaryMap.put("CHOIR_AAHS", "52");
        dictionaryMap.put("VOICE_OOHS", "53");
        dictionaryMap.put("SYNTH_VOICE", "54");
        dictionaryMap.put("ORCHESTRA_HIT", "55");
        dictionaryMap.put("TRUMPET", "56");
        dictionaryMap.put("TROMBONE", "57");
        dictionaryMap.put("TUBA", "58");
        dictionaryMap.put("MUTED_TRUMPET", "59");

        dictionaryMap.put("FRENCH_HORN", "60");
        dictionaryMap.put("BRASS_SECTION", "61");
        dictionaryMap.put("SYNTHBRASS_1", "62");
        dictionaryMap.put("SYNTHBRASS_2", "63");
        dictionaryMap.put("SOPRANO_SAX", "64");
        dictionaryMap.put("ALTO_SAX", "65");
        dictionaryMap.put("TENOR_SAX", "66");
        dictionaryMap.put("BARITONE_SAX", "67");
        dictionaryMap.put("OBOE", "68");
        dictionaryMap.put("ENGLISH_HORN", "69");

        dictionaryMap.put("BASSOON", "70");
        dictionaryMap.put("CLARINET", "71");
        dictionaryMap.put("PICCOLO", "72");
        dictionaryMap.put("FLUTE", "73");
        dictionaryMap.put("RECORDER", "74");
        dictionaryMap.put("PAN_FLUTE", "75");
        dictionaryMap.put("BLOWN_BOTTLE", "76");
        dictionaryMap.put("SKAKUHACHI", "77");
        dictionaryMap.put("WHISTLE", "78");
        dictionaryMap.put("OCARINA", "79");

        dictionaryMap.put("LEAD_SQUARE", "80");
        dictionaryMap.put("SQUARE", "80");
        dictionaryMap.put("LEAD_SAWTOOTH", "81");
        dictionaryMap.put("SAWTOOTH", "81");
        dictionaryMap.put("LEAD_CALLIOPE", "82");
        dictionaryMap.put("CALLIOPE", "82");
        dictionaryMap.put("LEAD_CHIFF", "83");
        dictionaryMap.put("CHIFF", "83");
        dictionaryMap.put("LEAD_CHARANG", "84");
        dictionaryMap.put("CHARANG", "84");
        dictionaryMap.put("LEAD_VOICE", "85");
        dictionaryMap.put("VOICE", "85");
        dictionaryMap.put("LEAD_FIFTHS", "86");
        dictionaryMap.put("FIFTHS", "86");
        dictionaryMap.put("LEAD_BASSLEAD", "87");
        dictionaryMap.put("BASSLEAD", "87");
        dictionaryMap.put("PAD_NEW_AGE", "88");
        dictionaryMap.put("NEW_AGE", "88");
        dictionaryMap.put("PAD_WARM", "89");
        dictionaryMap.put("WARM", "89");

        dictionaryMap.put("PAD_POLYSYNTH", "90");
        dictionaryMap.put("POLYSYNTH", "90");
        dictionaryMap.put("PAD_CHOIR", "91");
        dictionaryMap.put("CHOIR", "91");
        dictionaryMap.put("PAD_BOWED", "92");
        dictionaryMap.put("BOWED", "92");
        dictionaryMap.put("PAD_METALLIC", "93");
        dictionaryMap.put("METALLIC", "93");
        dictionaryMap.put("PAD_HALO", "94");
        dictionaryMap.put("HALO", "94");
        dictionaryMap.put("PAD_SWEEP", "95");
        dictionaryMap.put("SWEEP", "95");
        dictionaryMap.put("FX_RAIN", "96");
        dictionaryMap.put("RAIN", "96");
        dictionaryMap.put("FX_SOUNDTRACK", "97");
        dictionaryMap.put("SOUNDTRACK", "97");
        dictionaryMap.put("FX_CRYSTAL", "98");
        dictionaryMap.put("CRYSTAL", "98");
        dictionaryMap.put("FX_ATMOSPHERE", "99");
        dictionaryMap.put("ATMOSPHERE", "99");

        dictionaryMap.put("FX_BRIGHTNESS", "100");
        dictionaryMap.put("BRIGHTNESS", "100");
        dictionaryMap.put("FX_GOBLINS", "101");
        dictionaryMap.put("GOBLINS", "101");
        dictionaryMap.put("FX_ECHOES", "102");
        dictionaryMap.put("ECHOES", "102");
        dictionaryMap.put("FX_SCI-FI", "103");
        dictionaryMap.put("SCI-FI", "103");
        dictionaryMap.put("SITAR", "104");
        dictionaryMap.put("BANJO", "105");
        dictionaryMap.put("SHAMISEN", "106");
        dictionaryMap.put("KOTO", "107");
        dictionaryMap.put("KALIMBA", "108");
        dictionaryMap.put("BAGPIPE", "109");

        dictionaryMap.put("FIDDLE", "110");
        dictionaryMap.put("SHANAI", "111");
        dictionaryMap.put("TINKLE_BELL", "112");
        dictionaryMap.put("AGOGO", "113");
        dictionaryMap.put("STEEL_DRUMS", "114");
        dictionaryMap.put("WOODBLOCK", "115");
        dictionaryMap.put("TAIKO_DRUM", "116");
        dictionaryMap.put("MELODIC_TOM", "117");
        dictionaryMap.put("SYNTH_DRUM", "118");
        dictionaryMap.put("REVERSE_CYMBAL", "119");

        dictionaryMap.put("GUITAR_FRET_NOISE", "120");
        dictionaryMap.put("BREATH_NOISE", "121");
        dictionaryMap.put("SEASHORE", "122");
        dictionaryMap.put("BIRD_TWEET", "123");
        dictionaryMap.put("TELEPHONE_RING", "124");
        dictionaryMap.put("HELICOPTER", "125");
        dictionaryMap.put("APPLAUSE", "126");
        dictionaryMap.put("GUNSHOT", "127");

        //
        // Percussion names
        //
        dictionaryMap.put("ACOUSTIC_BASS_DRUM", "35");
        dictionaryMap.put("BASS_DRUM", "36");
        dictionaryMap.put("SIDE_STICK", "37");
        dictionaryMap.put("ACOUSTIC_SNARE", "38");
        dictionaryMap.put("HAND_CLAP", "39");

        dictionaryMap.put("ELECTRIC_SNARE", "40");
        dictionaryMap.put("LOW_FLOOR_TOM", "41");
        dictionaryMap.put("CLOSED_HI_HAT", "42");
        dictionaryMap.put("HIGH_FLOOR_TOM", "43");
        dictionaryMap.put("PEDAL_HI_HAT", "44");
        dictionaryMap.put("LOW_TOM", "45");
        dictionaryMap.put("OPEN_HI_HAT", "46");
        dictionaryMap.put("LOW_MID_TOM", "47");
        dictionaryMap.put("HI_MID_TOM", "48");
        dictionaryMap.put("CRASH_CYMBAL_1", "49");

        dictionaryMap.put("HIGH_TOM", "50");
        dictionaryMap.put("RIDE_CYMBAL_1", "51");
        dictionaryMap.put("CHINESE_CYMBAL", "52");
        dictionaryMap.put("RIDE_BELL", "53");
        dictionaryMap.put("TAMBOURINE", "54");
        dictionaryMap.put("SPLASH_CYMBAL", "55");
        dictionaryMap.put("COWBELL", "56");
        dictionaryMap.put("CRASH_CYMBAL_2", "57");
        dictionaryMap.put("VIBRASLAP", "58");
        dictionaryMap.put("RIDE_CYMBAL_2", "59");

        dictionaryMap.put("HI_BONGO", "60");
        dictionaryMap.put("LOW_BONGO", "61");
        dictionaryMap.put("MUTE_HI_CONGA", "62");
        dictionaryMap.put("OPEN_HI_CONGA", "63");
        dictionaryMap.put("LOW_CONGA", "64");
        dictionaryMap.put("HIGH_TIMBALE", "65");
        dictionaryMap.put("LOW_TIMBALE", "66");
        dictionaryMap.put("HIGH_AGOGO", "67");
        dictionaryMap.put("LOW_AGOGO", "68");
        dictionaryMap.put("CABASA", "69");

        dictionaryMap.put("MARACAS", "70");
        dictionaryMap.put("SHORT_WHISTLE", "71");
        dictionaryMap.put("LONG_WHISTLE", "72");
        dictionaryMap.put("SHORT_GUIRO", "73");
        dictionaryMap.put("LONG_GUIRO", "74");
        dictionaryMap.put("CLAVES", "75");
        dictionaryMap.put("HI_WOOD_BLOCK", "76");
        dictionaryMap.put("LOW_WOOD_BLOCK", "77");
        dictionaryMap.put("MUTE_CUICA", "78");
        dictionaryMap.put("OPEN_CUICA", "79");

        dictionaryMap.put("MUTE_TRIANGLE", "80");
        dictionaryMap.put("OPEN_TRIANGLE", "81");
    }
}