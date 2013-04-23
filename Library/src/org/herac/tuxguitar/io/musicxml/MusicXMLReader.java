package org.herac.tuxguitar.io.musicxml;

import cz.cvut.fel.kinclma1.MusicStringTone;
import org.herac.tuxguitar.player.base.MidiInstrument;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 1.12.12
 * Time: 17:52
 * To change this template use File | Settings | File Templates.
 */
public class MusicXMLReader {

    private TGFactory factory;
    private InputStream stream;
    private TGSong song;
    private TGSongManager manager;
    private Document document;

    private HashMap<String, Integer> durations = new HashMap<String, Integer>(7);
    private HashMap<String, Integer> tones = new HashMap<String, Integer>(8);
    private HashMap<String,TGTrack> trackMap;
    private HashMap<String, Integer> drumInstruments = null;
    private Element attributes;
    private Element direction;

    public MusicXMLReader(TGFactory factory, InputStream stream) {
        this.factory = factory;
        this.stream = stream;
        manager = new TGSongManager();
        durations.put("whole", 1);
        durations.put("half", 2);
        durations.put("quarter", 4);
        durations.put("eighth", 8);
        durations.put("16th", 16);
        durations.put("32nd", 32);
        durations.put("64th", 64);
        tones.put("C", 0);
        tones.put("D", 2);
        tones.put("E", 4);
        tones.put("F", 5);
        tones.put("G", 7);
        tones.put("A", 9);
        tones.put("B", 11);
    }

    public TGSong readSong() {
        song = factory.newSong();
        manager.setSong(song);
        trackMap = new HashMap<String, TGTrack>();

        if (!createDocument()) {
            return song;
        }

        Element root = document.getDocumentElement();

        if (!parsePartList(root)) {
            return song;
        }

        parseParts(root);

        addTracksToTGSong();

        addMeasureHeaders();

        return song;
    }

    private boolean createDocument() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setIgnoringElementContentWhitespace(true);
        try {
            document = docFactory.newDocumentBuilder().parse(stream);
        } catch (Exception e) {
            System.out.println("Cannot parse MusicXML file, file is probably not a valid XML");
            return false;
        }

        return true;
    }

    private boolean parsePartList(Element root) {
        NodeList partLists = root.getElementsByTagName("part-list");
        if (partLists.getLength() > 0) {
            addTracks((Element) partLists.item(0));
            return true;
        } else {
            System.out.println("Invalid MusicXML file");
            return false;
        }
    }

    private void parseParts(Element root) {
        NodeList parts = root.getElementsByTagName("part");
        for (int i = 0; i < parts.getLength(); i ++) {
            addMeasures((Element) parts.item(i));
        }
    }

    private void addTracksToTGSong() {
        List<TGTrack> tracks = new ArrayList<TGTrack>(trackMap.size());
        for (TGTrack track : trackMap.values()) {
            tracks.add(track);
        }
        Collections.sort(tracks, new Comparator<TGTrack>() {
            @Override
            public int compare(TGTrack o1, TGTrack o2) {
                return o1.getChannel().getChannel() - o2.getChannel().getChannel();
            }
        });
        for (TGTrack track : tracks) {
            if (!track.isPercussionTrack()) {
                track.setName(MidiInstrument.INSTRUMENT_LIST[track.getChannel().getInstrument()].getName());
            }
            song.addTrack(track);
        }
    }

    private void addMeasureHeaders() {
        TGTrack track = manager.getTrack(0);
        for (int i = 0; i < track.countMeasures(); i ++) {
            song.addMeasureHeader(track.getMeasure(i).getHeader());
        }
    }

    private void addTracks(Element partList) {
        NodeList parts = partList.getElementsByTagName("score-part");
        for (int i = 0; i < parts.getLength(); i ++) {
            addTrack((Element) parts.item(i));
        }
    }

    private void addTrack(Element scorePart) {
        String id = scorePart.getAttribute("id");
        if (id != null) {
            TGTrack track = factory.newTrack();
            boolean channelSet;

            track.setName(scorePart.getElementsByTagName("part-name").item(0).getFirstChild().getNodeValue());

            channelSet = setTrackMidi(scorePart, track);

            if (!channelSet) {
                guessInstrument(scorePart, track);
            }

            trackMap.put(id, track);
        }
    }

    private boolean setTrackMidi(Element scorePart, TGTrack track) {
        boolean channelSet = false;
        NodeList midiList = scorePart.getElementsByTagName("midi-instrument");
        if (midiList.getLength() > 0) {
            channelSet = configureChannel(track, (Element) midiList.item(0));
            if (track.isPercussionTrack()) {
                for (int i = 0; i < midiList.getLength(); i ++) {
                    addDrumsToMap((Element) midiList.item(i));
                }
            }
        }
        return channelSet;
    }

    private void guessInstrument(Element scorePart, TGTrack track) {
        NodeList instruments = scorePart.getElementsByTagName("score-instrument");
        if (instruments.getLength() > 0) {
            String instrument = scorePart.getElementsByTagName("score-instrument").item(0).getFirstChild().getNodeValue();
            for (short j = 0; j < MidiInstrument.INSTRUMENT_LIST.length; j ++) {
                if (MidiInstrument.INSTRUMENT_LIST[j].getName().equalsIgnoreCase(instrument)) {
                    track.getChannel().setInstrument(j);
                }
            }
        }
    }

    private boolean configureChannel(TGTrack track, Element midi) {
        NodeList channelSettings = midi.getChildNodes();
        boolean configured = false;

        for (int i = 0; i < channelSettings.getLength(); i ++) {
            Node setting = channelSettings.item(i);
            if (setting.getNodeName().equals("midi-channel")) {
                track.getChannel().setChannel((short) (Short.parseShort(setting.getFirstChild().getNodeValue()) - 1));
            } else if (setting.getNodeName().equals("midi-program")) {
                track.getChannel().setInstrument((short) (Short.parseShort(setting.getFirstChild().getNodeValue()) - 1));
                configured = true;
            }
        }
        return configured;
    }

    private void addDrumsToMap(Element midi) {
        NodeList instruments = midi.getElementsByTagName("midi-unpitched");
        if (instruments.getLength() > 0) {
            Node instrument = instruments.item(0);

            if (drumInstruments == null) {
                drumInstruments = new HashMap<String, Integer>();
            }

            drumInstruments.put(midi.getAttribute("id"), Integer.parseInt(instrument.getFirstChild().getNodeValue()));
        }
    }

    private void addMeasures(Element part) {
        NodeList measures = part.getElementsByTagName("measure");
        TGTrack track = trackMap.get(part.getAttribute("id"));
        for (int i = 0; i < measures.getLength(); i ++) {
            addMeasure((Element) measures.item(i), track);
        }
    }

    private void addMeasure(Element srcMeasure, TGTrack track) {
        TGMeasure measure = factory.newMeasure(factory.newHeader());
        track.addMeasure(measure);

        NodeList atts = srcMeasure.getElementsByTagName("attributes");
        setMeasureAttributes(atts.getLength() > 0 ? (Element) atts.item(0) : attributes, measure);

        NodeList tmp = srcMeasure.getElementsByTagName("direction");
        setTempo(tmp.getLength() > 0 ? (Element) tmp.item(0) : direction, measure);

        String measureNumber = srcMeasure.getAttribute("number");
        if (!measureNumber.isEmpty()) {
            measure.getHeader().setNumber(Integer.parseInt(measureNumber));
        }

        addBeats(srcMeasure.getElementsByTagName("note"), measure);
    }

    private void setMeasureAttributes(Element attributes, TGMeasure measure) {
        this.attributes = attributes;

        setMeasureClef(measure);

        setInstrumentStrings(measure);

        setTimeSignature(measure);
    }

    private void setMeasureClef(TGMeasure measure) {
        NodeList clefs = attributes.getElementsByTagName("clef");
        if (clefs.getLength() > 0) {
            NodeList signs = ((Element) clefs.item(0)).getElementsByTagName("sign");
            String clef;
            if (signs.getLength() > 0) {
                clef = signs.item(0).getFirstChild().getNodeValue();
            } else {
                clef = "";
            }
            measure.setClef(clef.equals("F") ? TGMeasure.CLEF_BASS : TGMeasure.DEFAULT_CLEF);
        }
    }

    private void setInstrumentStrings(TGMeasure measure) {
        if (measure.getTrack().isPercussionTrack()) {
            List<TGString> strings = manager.createDefaultInstrumentStrings();
            for (TGString string : strings) {
                string.setValue(0);
            }
            measure.getTrack().setStrings(strings);
        } else if (measure.getClef() == TGMeasure.CLEF_BASS) {
            ArrayList<TGString> strings = new ArrayList<TGString>(4);
            String[] stringArray = new String[]{"G3", "D3", "A2", "E2"};
            for (int i = 0; i < 4; i ++) {
                TGString string = factory.newString();
                string.setNumber(i + 1);
                string.setValue(new MusicStringTone(stringArray[i]).toInt());
                strings.add(string);
            }
            measure.getTrack().setStrings(strings);
        } else {
            measure.getTrack().setStrings(manager.createDefaultInstrumentStrings());
        }
    }

    private void setTimeSignature(TGMeasure measure) {
        NodeList times = attributes.getElementsByTagName("time");

        if (times.getLength() > 0) {
            Element timeSignature = (Element) times.item(0);

            TGTimeSignature signature = measure.getTimeSignature();

            NodeList beats = timeSignature.getElementsByTagName("beats");
            if (beats.getLength() > 0) {
                signature.setNumerator(Integer.parseInt(beats.item(0).getFirstChild().getNodeValue()));
            }

            NodeList beatTypes = timeSignature.getElementsByTagName("beat-type");
            if (beatTypes.getLength() > 0) {
                signature.getDenominator().setValue(Integer.parseInt(beatTypes.item(0).getFirstChild().getNodeValue()));
            }
        }
    }

    private void setTempo(Element direction, TGMeasure measure) {
        this.direction = direction;
        measure.getTempo().setValue(Integer.parseInt(((Element) direction.getElementsByTagName("sound")
                .item(0)).getAttribute("tempo")));
    }

    private void addBeats(NodeList notes, TGMeasure measure) {
        List<Element> beat = null;
        for (int i = 0; i < notes.getLength(); i ++) {
            Element note = (Element) notes.item(i);
            if (note.getElementsByTagName("chord").getLength() == 0) {
                if (beat != null && !beat.isEmpty()) {
                    addBeat(beat, measure);
                }
                beat = new LinkedList<Element>();
            }
            if (beat != null) {
                beat.add(note);
            }
        }
        if (!(beat != null && beat.isEmpty())) addBeat(beat, measure);
    }

    private void addBeat(List<Element> beat, TGMeasure measure) {
        TGBeat tgBeat = factory.newBeat();

        setBeatDuration(tgBeat, beat.get(0));

        if (beat.size() == 1 && beat.get(0).getElementsByTagName("rest").getLength() > 0) {
            tgBeat.getVoice(0).setEmpty(false);
        } else {
            addNotesToBeat(beat, measure, tgBeat);
        }

        measure.addBeat(tgBeat);
    }

    private void setBeatDuration(TGBeat tgBeat, Element note) {
        TGDuration duration = tgBeat.getVoice(0).getDuration();

        NodeList types = note.getElementsByTagName("type");
        if (types.getLength() > 0) {
            duration.setValue(durations.get(types.item(0).getFirstChild().getNodeValue()));
        }

        duration.setDotted(note.getElementsByTagName("dot").getLength() > 0);
    }

    private void addNotesToBeat(List<Element> beat, TGMeasure measure, TGBeat tgBeat) {
        List<Integer> toneInts = tonesToInts(beat);
        List<TGString> strings = measure.getTrack().getStrings();
        int stringNum = 1;
        for (Integer tone : toneInts) {
            while (tone < strings.get(stringNum - 1).getValue()) {
                stringNum ++;
            }
            TGNote note = factory.newNote();
            note.setValue(tone - strings.get(stringNum - 1).getValue());
            note.setString(stringNum);
            tgBeat.getVoice(0).addNote(note);
            stringNum ++;
        }
    }

    private List<Integer> tonesToInts(List<Element> notes) {
        ArrayList<Integer> noteList = new ArrayList<Integer>(notes.size());
        int val;
        for (Element note : notes) {
            if (note.getElementsByTagName("pitch").getLength() > 0) {
                NodeList pitchList = note.getElementsByTagName("pitch");
                if (pitchList.getLength() > 0) {
                    Element pitch = (Element) pitchList.item(0);

                    NodeList steps = pitch.getElementsByTagName("step");
                    NodeList octaves = pitch.getElementsByTagName("octave");
                    if (steps.getLength() > 0 && octaves.getLength() > 0) {
                        val = tones.get(steps.item(0).getFirstChild().getNodeValue());
                        val += 12 * Integer.parseInt(octaves.item(0).getFirstChild().getNodeValue());

                        if (pitch.getElementsByTagName("alter").getLength() > 0) {
                            val ++;
                        }

                        noteList.add(val);
                    }
                }
            } else if (note.getElementsByTagName("unpitched").getLength() > 0) {
                NodeList instruments = note.getElementsByTagName("instrument");
                if (instruments.getLength() > 0) {
                    Element drum = (Element) note.getElementsByTagName("instrument").item(0);
                    noteList.add(drumInstruments.get(drum.getAttribute("id")));
                }
            }
        }
        Collections.sort(noteList, Collections.reverseOrder());
        return noteList;
    }
}
