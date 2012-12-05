package org.herac.tuxguitar.io.musicxml;

import cz.cvut.fel.kinclma1.MusicStringTone;
import org.herac.tuxguitar.player.base.MidiInstrument;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGMeasureManager;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.managers.TGTrackManager;
import org.herac.tuxguitar.song.models.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
        this.manager = new TGSongManager();
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
    //todo check null everywhere
    //todo rests and twice as long measures
    public TGSong readSong() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setIgnoringElementContentWhitespace(true);
        try {
            document = docFactory.newDocumentBuilder().parse(stream);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        song = factory.newSong();
        manager.setSong(song);
        trackMap = new HashMap<String, TGTrack>();

        Element root = document.getDocumentElement();

        addTracks((Element) root.getElementsByTagName("part-list").item(0));
        NodeList parts = root.getElementsByTagName("part");
        for (int i = 0; i < parts.getLength(); i ++) {
            addMeasures((Element) parts.item(i));
        }
        for (TGTrack track : trackMap.values()) {
            song.addTrack(track);
        }
        TGTrack track = manager.getTrack(0);
        for (int i = 0; i < track.countMeasures(); i ++) {
            song.addMeasureHeader(track.getMeasure(i).getHeader());
        }
        song.setName("Song");

        return song;
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
        manager.getMeasureManager().moveOutOfBoundsBeatsToNewMeasure(measure, false);
        track.addMeasure(measure);

        NodeList atts = srcMeasure.getElementsByTagName("attributes");
        if (atts.getLength() > 0) {
            setMeasureAttributes((Element) atts.item(0), measure);
        } else {
            setMeasureAttributes(attributes, measure);
        }

        NodeList tmp = srcMeasure.getElementsByTagName("direction");
        if(tmp.getLength() > 0) {
            setTempo((Element) tmp.item(0), measure);
        } else {
            setTempo(direction, measure);
        }

        measure.getHeader().setNumber(Integer.parseInt(srcMeasure.getAttribute("number")));
        addNotes(srcMeasure.getElementsByTagName("note"), measure);
    }

    private void addNotes(NodeList notes, TGMeasure measure) {
        List<Element> beat = null;
        for (int i = 0; i < notes.getLength(); i ++) {
            Element note = (Element) notes.item(i);
            if (note.getElementsByTagName("chord").getLength() == 0) {
                if (beat != null) {
                    addBeat(beat, measure);
                }
                beat = new LinkedList<Element>();
            }
            beat.add(note);
        }
        if (!beat.isEmpty()) addBeat(beat, measure);
    }

    private void addBeat(List<Element> beat, TGMeasure measure) {
        TGBeat tgBeat = factory.newBeat();
        tgBeat.getVoice(0).getDuration().setValue(durations.get(beat.get(0).getElementsByTagName("type")
                .item(0).getFirstChild().getNodeValue()));
        tgBeat.getVoice(0).getDuration().setDotted(beat.get(0).getElementsByTagName("dot").getLength() > 0);
        if (beat.size() == 1 && beat.get(0).getElementsByTagName("rest").getLength() > 0) {
            tgBeat.getVoice(0).setEmpty(false);
        } else {
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
        measure.addBeat(tgBeat);
    }

    private List<Integer> tonesToInts(List<Element> notes) {
        ArrayList<Integer> noteList = new ArrayList<Integer>(notes.size());
        int val;
        for (Element note : notes) {
            if (note.getElementsByTagName("pitch").getLength() > 0) {
                Element pitch = (Element) note.getElementsByTagName("pitch").item(0);
                val = tones.get(pitch.getElementsByTagName("step").item(0).getFirstChild().getNodeValue());
                val += 12 * Integer.parseInt(pitch.getElementsByTagName("octave").item(0).getFirstChild().getNodeValue());
                if (pitch.getElementsByTagName("alter").getLength() != 0) {
                    val ++;
                }
                noteList.add(val);
            } else if (note.getElementsByTagName("unpitched").getLength() > 0) {
                Element drum = (Element) note.getElementsByTagName("instrument").item(0);
                noteList.add(drumInstruments.get(drum.getAttribute("id")));
            }

        }
        Collections.sort(noteList, Collections.reverseOrder());
        return noteList;
    }

    private void setTempo(Element direction, TGMeasure measure) {
        this.direction = direction;
        measure.getTempo().setValue(Integer.parseInt(((Element) direction.getElementsByTagName("sound")
                .item(0)).getAttribute("tempo")));
    }

    private void setMeasureAttributes(Element attributes, TGMeasure measure) {
        this.attributes = attributes;
        NodeList clefs = attributes.getElementsByTagName("clef");
        if (clefs.getLength() > 0) {
            String clef = ((Element) clefs.item(0))
                    .getElementsByTagName("sign").item(0).getFirstChild().getNodeValue();
            measure.setClef(clef.equals("F") ? TGMeasure.CLEF_BASS : TGMeasure.DEFAULT_CLEF);
        }

        if (measure.getTrack().getChannel().isPercussionChannel()) {
            List<TGString> strings = manager.createDefaultInstrumentStrings();
            for (TGString string : strings) {
                string.setValue(0);
            }
            measure.getTrack().setStrings(strings);
        } else {
            if (measure.getClef() == TGMeasure.DEFAULT_CLEF) {
                measure.getTrack().setStrings(manager.createDefaultInstrumentStrings());
            } else if (measure.getClef() == TGMeasure.CLEF_BASS) {
                ArrayList<TGString> strings = new ArrayList<TGString>(4);
                String[] stringArray = new String[]{"G3", "D3", "A2", "E2"};
                for (int i = 0; i < 4; i ++) {
                    TGString string = factory.newString();
                    string.setNumber(i + 1);
                    string.setValue(new MusicStringTone(stringArray[i]).toInteger());
                    strings.add(string);
                }
                measure.getTrack().setStrings(strings);
            }
        }

        Element timeSignature = (Element) attributes.getElementsByTagName("time").item(0);
        TGTimeSignature signature = measure.getTimeSignature();
        signature.setNumerator(
                Integer.parseInt(timeSignature.getElementsByTagName("beats").item(0).getFirstChild().getNodeValue()));
        signature.getDenominator().setValue(
                Integer.parseInt(timeSignature.getElementsByTagName("beat-type").item(0).getFirstChild().getNodeValue()));
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
            NodeList midiList = scorePart.getElementsByTagName("midi-instrument");
            channelSet = configureChannel(track, (Element) midiList.item(0));
            if (track.isPercussionTrack()) {
                for (int i = 0; i < midiList.getLength(); i ++) {
                    addDrumsToMap((Element) midiList.item(i));
                }
            }

            if (!channelSet) {
                String instrument = scorePart.getElementsByTagName("score-instrument").item(0).getFirstChild().getNodeValue();
                for (short j = 0; j < MidiInstrument.INSTRUMENT_LIST.length; j ++) {
                    if (MidiInstrument.INSTRUMENT_LIST[j].getName().equalsIgnoreCase(instrument)) {
                        track.getChannel().setInstrument(j);
                    }
                }
            }

            trackMap.put(id, track);
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
        Node setting = midi.getElementsByTagName("midi-unpitched").item(0);
        if (drumInstruments == null) {
            drumInstruments = new HashMap<String, Integer>();
        }
        drumInstruments.put(midi.getAttribute("id"),
                Integer.parseInt(setting.getFirstChild().getNodeValue()));
    }
}
