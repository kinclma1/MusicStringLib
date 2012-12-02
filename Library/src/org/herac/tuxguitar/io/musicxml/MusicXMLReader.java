package org.herac.tuxguitar.io.musicxml;

import org.herac.tuxguitar.player.base.MidiInstrument;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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

    private HashMap<String,TGTrack> trackMap;
    private HashMap<String, Integer> drumInstruments = null;

    public MusicXMLReader(TGFactory factory, InputStream stream) {
        this.factory = factory;
        this.stream = stream;
        this.manager = new TGSongManager();
    }

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

        Node node = document.getDocumentElement();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i ++) {
            Node child = nodeList.item(i);
            if (child.getNodeName().equals("part-list")) {
                addTracks(child);
            } else if (child.getNodeName().equals("part")) {
                addMeasures(child);
            }
        }

        return null;
    }

    private void addMeasures(Node part) {
        NodeList measures = part.getChildNodes();
        TGTrack track = trackMap.get(part.getAttributes().getNamedItem("id"));
        for (int i = 0; i < measures.getLength(); i ++) {
            Node measure = measures.item(i);
            if (measure.getNodeName().equals("measure")) {
                addMeasure(measure, track);
            }
        }
    }

    private void addMeasure(Node srcMeasure, TGTrack track) {
        NodeList components = srcMeasure.getChildNodes();
        for (int i = 0; i < components.getLength(); i ++) {
            Node component = components.item(i);
            if (component.getNodeName().equals("note")) {
                //add note
            } else if (component.getNodeName().equals("direction")) {
                //set tempo
            } else if (component.getNodeName().equals("attributes")) {
                //set measure clef and time signature
            }
        }
    }

    private void addTracks(Node partList) {
        NodeList parts = partList.getChildNodes();
        for (int i = 0; i < parts.getLength(); i ++) {
            Node child = parts.item(i);
            if(child.getNodeName().equals("score-part")) {
                addTrack(parts.item(i));
            }
        }
    }

    private void addTrack(Node scorePart) {
        if (scorePart.hasAttributes()) {
            Node id = scorePart.getAttributes().getNamedItem("id");
            if (id != null) {
                TGTrack track = factory.newTrack();
                NodeList nodes = scorePart.getChildNodes();
                boolean channelSet = false;
                int indexOfInstrument = -1;
                for(int i = 0; i < nodes.getLength(); i ++) {
                    Node item = nodes.item(i);
                    if (item.getNodeName().equals("part-name")) {
                        track.setName(item.getFirstChild().getNodeValue());
                    } else if (item.getNodeName().equals("midi-instrument")) {
                        channelSet = configureChannel(track, item);
                    } else if (item.getNodeName().equals("score-instrument")) {
                        indexOfInstrument = i;
                    }
                }
                if (!channelSet && indexOfInstrument >= 0) {
                    for (short j = 0; j < MidiInstrument.INSTRUMENT_LIST.length; j ++) {
                        if (MidiInstrument.INSTRUMENT_LIST[j].getName()
                                .equalsIgnoreCase(nodes.item(indexOfInstrument).getFirstChild().getNodeValue())) {
                            track.getChannel().setInstrument(j);
                        }
                    }
                }
                trackMap.put(id.getNodeValue(), track);
            }
        }
    }

    private boolean configureChannel(TGTrack track, Node midi) {
        NodeList channelSettings = midi.getChildNodes();
        boolean configured = false;
        for (int i = 0; i < channelSettings.getLength(); i ++) {
            Node setting = channelSettings.item(i);
            if (setting.getNodeName().equals("midi-channel")) {
                track.getChannel().setChannel(Short.parseShort(setting.getFirstChild().getNodeValue()));
            } else if (setting.getNodeName().equals("midi-program")) {
                track.getChannel().setInstrument(Short.parseShort(setting.getFirstChild().getNodeValue()));
                configured = true;
            } else if (setting.getNodeName().equals("midi-unpitched")) {
                if (drumInstruments == null) {
                    drumInstruments = new HashMap<String, Integer>();
                }
                drumInstruments.put(midi.getAttributes().getNamedItem("id").getNodeValue(),
                        Integer.parseInt(setting.getFirstChild().getNodeValue()));
            }
        }
        return configured;
    }
}
