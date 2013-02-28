package org.herac.tuxguitar.io.musicxml;

import cz.cvut.fel.kinclma1.Drum;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.player.base.MidiInstrument;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;

public class MusicXMLWriter {

    private static final String[] NOTE_NAMES = new String[]{"C", "D", "E", "F", "G", "A", "B"};
    private static final int NOTE_SHARPS[] = new int[]{0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6};
    private static final int NOTE_FLATS[] = new int[]{0, 1, 1, 2, 2, 3, 4, 4, 5, 5, 6, 6};
    private static final boolean[] NOTE_ALTERATIONS = new boolean[]{false, true, false, true, false, false, true, false, true, false, true, false};
    private static final String[] DURATION_NAMES = new String[]{"whole", "half", "quarter", "eighth", "16th", "32nd", "64th",};
    private static final int DURATION_DIVISIONS = (int) TGDuration.QUARTER_TIME;
    private static final int[] DURATION_VALUES = new int[]{
        DURATION_DIVISIONS * 4, // WHOLE
        DURATION_DIVISIONS * 2, // HALF
            DURATION_DIVISIONS, // QUARTER
        DURATION_DIVISIONS / 2, // EIGHTH
        DURATION_DIVISIONS / 4, // SIXTEENTH
        DURATION_DIVISIONS / 8, // THIRTY_SECOND
        DURATION_DIVISIONS / 16, // SIXTY_FOURTH
    };
    private TGSongManager manager;
    private OutputStream stream;
    private Document document;

    public MusicXMLWriter(OutputStream stream) {
        this.stream = stream;
    }

    public void writeSong(TGSong song) throws TGFileFormatException {
        try {
            manager = new TGSongManager();
            manager.setSong(song);
            document = newDocument();

            Node node = document.getDocumentElement();
            writeSong(node);
            saveDocument();

            stream.flush();
            stream.close();
        } catch (Throwable throwable) {
            throw new TGFileFormatException("Could not write song!.", throwable);
        }
    }

    private void writeSong(Node parent) {
        writePartList(parent);
        writeParts(parent);
    }

    private void writePartList(Node parent) {
        Node partList = addNode(parent, "part-list");

        Iterator<TGTrack> tracks = manager.getSong().getTracks();
        boolean drums = false;
        while (tracks.hasNext()) {
            TGTrack track = tracks.next();
            drums = track.getChannel().getChannel() == 9;

            Node scoreParts = addNode(partList, "score-part");
            addAttribute(scoreParts, "id", drums ? "Drums" : "P" + track.getNumber());

            addNode(scoreParts, "part-name", drums ? "Drums" : track.getName());
            if (!drums) {
                Node scoreInstrument = addAttribute(addNode(scoreParts, "score-instrument"), "id", "P" + track.getNumber() + "-I1");
                addNode(scoreInstrument, "instrument-name", MidiInstrument.INSTRUMENT_LIST[track.getChannel().getInstrument()].getName());

                Node midiInstrument = addAttribute(addNode(scoreParts, "midi-instrument"), "id", "P" + track.getNumber() + "-I1");
                addNode(midiInstrument, "midi-channel", Integer.toString(track.getChannel().getChannel() + 1));
                addNode(midiInstrument, "midi-program", Integer.toString(track.getChannel().getInstrument() + 1));
            } else {
                HashSet<Integer> instruments = new HashSet<Integer>();
                Iterator<TGMeasure> measureIterator = track.getMeasures();
                while (measureIterator.hasNext()) {
                    TGMeasure measure = measureIterator.next();
                    for (TGBeat beat : measure.getBeats()) {
                        TGVoice voice = beat.getVoice(0);
                        for (TGNote note : voice.getNotes()) {
                            instruments.add(note.getValue());
                        }
                    }
                }
                for (Integer instrument : instruments) {
                    Node scoreInstrument = addAttribute(addNode(scoreParts, "score-instrument"), "id", "Drums" + "-I" + instrument);
                    addNode(scoreInstrument, "instrument-name", Drum.fromInt(instrument).toString());
                }
                for (Integer instrument : instruments) {
                    Node midiInstrument = addAttribute(addNode(scoreParts, "midi-instrument"), "id", "Drums" + "-I" + instrument);
                    addNode(midiInstrument, "midi-channel", "10");
                    addNode(midiInstrument, "midi-program", "1");
                    addNode(midiInstrument, "midi-unpitched", instrument.toString());
                }
            }
        }
    }

    private void writeParts(Node parent) {
        Iterator<TGTrack> tracks = manager.getSong().getTracks();
        while (tracks.hasNext()) {
            TGTrack track = tracks.next();
            boolean drums = track.getChannel().getChannel() == 9;
            Node part = addAttribute(addNode(parent, "part"), "id", drums ? "Drums" : "P" + track.getNumber());

            TGMeasure previous = null;

            Iterator<TGMeasure> measures = track.getMeasures();
            while (measures.hasNext()) {
                TGMeasure measure = measures.next();
                Node measureNode = addAttribute(addNode(part, "measure"), "number", Integer.toString(measure.getNumber()));

                writeMeasureAttributes(measureNode, measure, previous);
                writeDirection(measureNode, measure, previous);
                writeBeats(measureNode, measure, drums);

                previous = measure;
            }
        }
    }

    private void writeMeasureAttributes(Node parent, TGMeasure measure, TGMeasure previous) {
        boolean drums = measure.getTrack().getChannel().getChannel() == 9;
        boolean first = previous == null;
        boolean timeSignatureChanges = (first || !measure.getTimeSignature().isEqual(previous.getTimeSignature()));

        if (timeSignatureChanges) {
            Node measureAttributes = addNode(parent, "attributes");
            if (first) {
                addNode(measureAttributes, "divisions", Integer.toString(DURATION_DIVISIONS));
                writeClef(measureAttributes, drums ? 9 : measure.getClef());
            }

            writeTimeSignature(measureAttributes, measure.getTimeSignature());
        }
    }

    private void writeTimeSignature(Node parent, TGTimeSignature ts) {
        Node node = addNode(parent, "time");
        addNode(node, "beats", Integer.toString(ts.getNumerator()));
        addNode(node, "beat-type", Integer.toString(ts.getDenominator().getValue()));
    }

    private void writeClef(Node parent, int clef) {
        Node node = addNode(parent, "clef");
        if(clef == 9) {
            addNode(node, "sign", "percussion");
        } else if (clef == TGMeasure.CLEF_BASS) {
            addNode(node, "sign", "F");
            addNode(node, "line", "4");
        } else {
            addNode(node, "sign", "G");
            addNode(node, "line", "2");
        }
    }

    private void writeDirection(Node parent, TGMeasure measure, TGMeasure previous) {
        boolean tempoChanges = (previous == null || measure.getTempo().getValue() != previous.getTempo().getValue());

        if (tempoChanges) {
            Node direction = addAttribute(addNode(parent, "direction"), "placement", "above");
            writeMeasureTempo(direction, measure.getTempo());
        }
    }

    private void writeMeasureTempo(Node parent, TGTempo tempo) {
        addAttribute(addNode(parent, "sound"), "tempo", Integer.toString(tempo.getValue()));
    }

    private void writeBeats(Node parent, TGMeasure measure, boolean drums) {
        int ks = measure.getKeySignature();
        int beatCount = measure.countBeats();
        for (int b = 0; b < beatCount; b++) {
            TGBeat beat = measure.getBeat(b);
            TGVoice voice = beat.getVoice(0);
            if (voice.isRestVoice()) {
                Node noteNode = addNode(parent, "note");
                addNode(noteNode, "rest");
                addNode(noteNode, "voice", "1");
                writeDuration(noteNode, voice.getDuration());
            } else {
                int noteCount = voice.countNotes();
                for (int n = 0; n < noteCount; n++) {
                    TGNote note = voice.getNote(n);

                    Node noteNode = addNode(parent, "note");
                    int value = (beat.getMeasure().getTrack().getString(note.getString()).getValue() + note.getValue());

                    String[] drumNoteAttributes = null;

                    if (!drums) {
                        Node pitchNode = addNode(noteNode, "pitch");
                        addNode(pitchNode, "step", NOTE_NAMES[ (ks <= 7 ? NOTE_SHARPS[value % 12] : NOTE_FLATS[value % 12])]);
                        addNode(pitchNode, "octave", Integer.toString(value / 12));
                        if (NOTE_ALTERATIONS[ value % 12]) {
                            addNode(pitchNode, "alter", (ks <= 7 ? "1" : "-1"));
                        }
                    } else {
                        drumNoteAttributes = drumNote(value);
                        Node pitchNode = addNode(noteNode, "unpitched");
                        addNode(pitchNode, "display-step", drumNoteAttributes[0]);
                        addNode(pitchNode, "display-octave", drumNoteAttributes[1]);
                    }
                    addNode(noteNode, "voice", "1");
                    writeDuration(noteNode, voice.getDuration());

                    if (n > 0) {
                        addNode(noteNode, "chord");
                    }
                    if (drums) {
                        Node instrument = addNode(noteNode, "instrument");
                        addAttribute(instrument,"id","Drums-I" + value);
                        if (drumNoteAttributes[2] != null) {
                            addNode(noteNode, "notehead", drumNoteAttributes[2]);
                        }
                    }
                }
            }
        }
    }

    private String[] drumNote(int drum) {
        if (drum == 35 || drum == 36) {
            return new String[]{"F","4",null};

        } else if (drum == 38 || drum == 40) {
            return new String[]{"C","5",null};

        } else if (drum == 37) {
            return new String[]{"C","5","x"};

        } else if (drum == 39) {
            return new String[]{"C","5","triangle"};

        } else if (drum == 41) {
            return new String[]{"E","4",null};

        } else if (drum == 42 || drum == 46) {
            return new String[]{"E","5","x"};

        } else if (drum == 43) {
            return new String[]{"G","4",null};

        } else if (drum == 44) {
            return new String[]{"D","4","x"};

        } else if (drum == 45) {
            return new String[]{"A","4",null};

        } else if (drum == 48) {
            return new String[]{"D","5",null};

        } else if (drum == 49 || drum == 57) {
            return new String[]{"G","5","circle-x"};

        } else if (drum == 50) {
            return new String[]{"F","5",null};

        } else if (drum == 51 || drum == 52 || drum == 59) {
            return new String[]{"G","5","x"};

        } else if (drum == 53) {
            return new String[]{"G","5",null};

        } else if (drum == 55) {
            return new String[]{"G","5","diamond"};

        } else if (drum == 56) {
            return new String[]{"G","5","triangle"};

        } else if (drum == 58) {
            return new String[]{"F","5","diamond"};

        } else {
            return new String[]{"B","4",null};
        }
    }

    private void writeDuration(Node parent, TGDuration duration) {
        int index = duration.getIndex();
        if (index >= 0 && index <= 6) {
            int value = (DURATION_VALUES[ index] * duration.getDivision().getTimes() / duration.getDivision().getEnters());
            if (duration.isDotted()) {
                value += (value / 2);
            }

            addNode(parent, "duration", Integer.toString(value));
            addNode(parent, "type", DURATION_NAMES[ index]);

            if (duration.isDotted()) {
                addNode(parent, "dot");
            }
        }
    }

    private Node addAttribute(Node node, String name, String value) {
        Attr attribute = document.createAttribute(name);
        attribute.setNodeValue(value);
        node.getAttributes().setNamedItem(attribute);
        return node;
    }

    private Node addNode(Node parent, String name) {
        Node node = document.createElement(name);
        parent.appendChild(node);
        return node;
    }

    private Node addNode(Node parent, String name, String content) {
        Node node = addNode(parent, name);
        node.setTextContent(content);
        return node;
    }

    private Document newDocument() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DocumentType type = builder.getDOMImplementation().createDocumentType("score-partwise",
                    "-//Recordare//DTD MusicXML 3.0 Partwise//EN", "http://www.musicxml.org/dtds/partwise.dtd");
            return builder.getDOMImplementation().createDocument("http://www.musicxml.org/xsd/MusicXML", "score-partwise", type);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    private void saveDocument() {
        try {
            TransformerFactory xformFactory = TransformerFactory.newInstance();
            Transformer idTransform = xformFactory.newTransformer();
            Source input = new DOMSource(document);
            Result output = new StreamResult(stream);
            idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
            idTransform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            idTransform.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Recordare//DTD MusicXML 3.0 Partwise//EN");
            idTransform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.musicxml.org/dtds/partwise.dtd");
            idTransform.transform(input, output);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
