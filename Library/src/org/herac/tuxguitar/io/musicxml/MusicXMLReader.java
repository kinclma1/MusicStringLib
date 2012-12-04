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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
    private TGSongManager manager;
    private Document document;

    private HashMap<String, Integer> durations = new HashMap<String, Integer>(7);
    private HashMap<String,TGTrack> trackMap;
    private HashMap<String, Integer> drumInstruments = null;

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
    }
    //todo check null everywhere
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

        manager.setSong(factory.newSong());
        trackMap = new HashMap<String, TGTrack>();

        Element root = document.getDocumentElement();

        addTracks((Element) root.getElementsByTagName("part-list").item(0));
        NodeList parts = root.getElementsByTagName("part");
        for (int i = 0; i < parts.getLength(); i ++) {
            addMeasures((Element) parts.item(i));
        }

        return null;
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
        setMeasureAttributes((Element) srcMeasure.getElementsByTagName("attributes").item(0), measure);
        setTempo((Element) srcMeasure.getElementsByTagName("direction").item(0), measure);
        addNotes(srcMeasure.getElementsByTagName("note"), measure);
        measure.getHeader().setNumber(Integer.parseInt(srcMeasure.getAttribute("number")));
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
    }

    private void addBeat(List<Element> beat, TGMeasure measure) {
        TGBeat tgBeat = factory.newBeat();
        tgBeat.getVoice(0).getDuration().setValue(durations.get(beat.get(0).getElementsByTagName("type")
                .item(0).getFirstChild().getNodeValue()));
        if (beat.size() == 1) {
            //todo add note  to highest possible string / rest
        } else {
            //todo add sorted notes to strings
        }
    }

    private void setTempo(Element direction, TGMeasure measure) {
        measure.getTempo().setValue(Integer.parseInt(((Element) direction.getElementsByTagName("sound")
                .item(0)).getAttribute("tempo")));
    }

    private void setMeasureAttributes(Element attributes, TGMeasure measure) {
        String clef = ((Element) attributes.getElementsByTagName("clef").item(0))
                .getElementsByTagName("sign").item(0).getFirstChild().getNodeValue();
        measure.setClef(clef.equals("F") ? TGMeasure.CLEF_BASS : TGMeasure.DEFAULT_CLEF);
        if (measure.getTrack().getChannel().isPercussionChannel()) {
            List<TGString> strings = manager.createDefaultInstrumentStrings();
            for (TGString string : strings) {
                string.setValue(0);
            }
            measure.getTrack().setStrings(strings);
        }
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
            channelSet = configureChannel(track, (Element) scorePart.getElementsByTagName("midi-instrument"));

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
                track.getChannel().setInstrument(Short.parseShort(setting.getFirstChild().getNodeValue()));
                configured = true;
            } else if (setting.getNodeName().equals("midi-unpitched")) {
                if (drumInstruments == null) {
                    drumInstruments = new HashMap<String, Integer>();
                }
                drumInstruments.put(midi.getAttribute("id"),
                        Integer.parseInt(setting.getFirstChild().getNodeValue()));
            }
        }
        return configured;
    }
}
