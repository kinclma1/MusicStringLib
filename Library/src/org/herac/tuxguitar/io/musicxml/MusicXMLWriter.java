package org.herac.tuxguitar.io.musicxml;

import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.player.base.MidiInstrument;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGVoice;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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

            Node node = addNode(document, "score-partwise");
            writeHeaders(node);
            writeSong(node);
            saveDocument();

            stream.flush();
            stream.close();
        } catch (Throwable throwable) {
            throw new TGFileFormatException("Could not write song!.", throwable);
        }
    }

    private void writeHeaders(Node parent) {
        writeWork(parent);
        writeIdentification(parent);
    }

    private void writeWork(Node parent) {
        addNode(addNode(parent, "work"), "work-title", manager.getSong().getName());
    }

    private void writeIdentification(Node parent) {
        Node identification = addNode(parent, "identification");
        addNode(addNode(identification, "encoding"), "software", "TuxGuitar");
        addAttribute(addNode(identification, "creator", manager.getSong().getAuthor()), "type", "composer");
    }

    private void writeSong(Node parent) {
        writePartList(parent);
        writeParts(parent);
    }

    private void writePartList(Node parent) {
        Node partList = addNode(parent, "part-list");

        Iterator<TGTrack> tracks = manager.getSong().getTracks();
        while (tracks.hasNext()) {
            TGTrack track = tracks.next();

            Node scoreParts = addNode(partList, "score-part");
            addAttribute(scoreParts, "id", "P" + track.getNumber());

            addNode(scoreParts, "part-name", track.getName());

            Node scoreInstrument = addAttribute(addNode(scoreParts, "score-instrument"), "id", "P" + track.getNumber() + "-I1");
            addNode(scoreInstrument, "instrument-name", MidiInstrument.INSTRUMENT_LIST[track.getChannel().getInstrument()].getName());

            Node midiInstrument = addAttribute(addNode(scoreParts, "midi-instrument"), "id", "P" + track.getNumber() + "-I1");
            addNode(midiInstrument, "midi-channel", Integer.toString(track.getChannel().getChannel() + 1));
            addNode(midiInstrument, "midi-program", Integer.toString(track.getChannel().getInstrument() + 1));
        }
    }

    private void writeParts(Node parent) {
        Iterator<TGTrack> tracks = manager.getSong().getTracks();
        while (tracks.hasNext()) {
            TGTrack track = tracks.next();
            Node part = addAttribute(addNode(parent, "part"), "id", "P" + track.getNumber());

            TGMeasure previous = null;

            Iterator<TGMeasure> measures = track.getMeasures();
            while (measures.hasNext()) {
                // TODO: Add multivoice support.
                TGMeasure measure = measures.next();
//                TGMeasure measure = new TGVoiceJoiner(this.manager.getFactory(), srcMeasure).process();
                Node measureNode = addAttribute(addNode(part, "measure"), "number", Integer.toString(measure.getNumber()));

                writeMeasureAttributes(measureNode, measure, previous);
                writeDirection(measureNode, measure, previous);
                writeBeats(measureNode, measure);

                previous = measure;
            }
        }
    }

    private void writeMeasureAttributes(Node parent, TGMeasure measure, TGMeasure previous) {
        boolean divisionChanges = (previous == null);
        boolean keyChanges = (previous == null || measure.getKeySignature() != previous.getKeySignature());
        boolean clefChanges = (previous == null || measure.getClef() != previous.getClef());
        boolean timeSignatureChanges = (previous == null || !measure.getTimeSignature().isEqual(previous.getTimeSignature()));
        boolean tuningChanges = (measure.getNumber() == 1);
        if (divisionChanges || keyChanges || clefChanges || timeSignatureChanges) {
            Node measureAttributes = addNode(parent, "attributes");
            if (divisionChanges) {
                addNode(measureAttributes, "divisions", Integer.toString(DURATION_DIVISIONS));
            }
            if (keyChanges) {
                writeKeySignature(measureAttributes, measure.getKeySignature());
            }
            if (clefChanges) {
                writeClef(measureAttributes, measure.getClef());
            }
            if (timeSignatureChanges) {
                writeTimeSignature(measureAttributes, measure.getTimeSignature());
            }
            if (tuningChanges) {
                writeTuning(measureAttributes, measure.getTrack());
            }
        }
    }

    private void writeTuning(Node parent, TGTrack track) {
        Node staffDetailsNode = addNode(parent, "staff-details");
        addNode(staffDetailsNode, "staff-lines", Integer.toString(track.stringCount()));
        for (int i = track.stringCount(); i > 0; i--) {
            TGString string = track.getString(i);
            Node stringNode = addNode(staffDetailsNode, "staff-tuning");
            addAttribute(stringNode, "line", Integer.toString((track.stringCount() - string.getNumber()) + 1));
            addNode(stringNode, "tuning-step", NOTE_NAMES[ NOTE_SHARPS[ (string.getValue() % 12)]]);
            addNode(stringNode, "tuning-octave", Integer.toString(string.getValue() / 12));
        }
    }

    private void writeTimeSignature(Node parent, TGTimeSignature ts) {
        Node node = addNode(parent, "time");
        addNode(node, "beats", Integer.toString(ts.getNumerator()));
        addNode(node, "beat-type", Integer.toString(ts.getDenominator().getValue()));
    }

    private void writeKeySignature(Node parent, int ks) {
        int value = ks;
        if (value != 0) {
            value = ((((ks - 1) % 7) + 1) * (ks > 7 ? -1 : 1));
        }
        Node key = addNode(parent, "key");
        addNode(key, "fifths", Integer.toString(value));
        addNode(key, "mode", "major");
    }

    private void writeClef(Node parent, int clef) {
        Node node = addNode(parent, "clef");
        if (clef == TGMeasure.CLEF_BASS) {
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

    private void writeBeats(Node parent, TGMeasure measure) {
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

                    Node pitchNode = addNode(noteNode, "pitch");
                    addNode(pitchNode, "step", NOTE_NAMES[ (ks <= 7 ? NOTE_SHARPS[value % 12] : NOTE_FLATS[value % 12])]);
                    addNode(pitchNode, "octave", Integer.toString(value / 12));
                    if (NOTE_ALTERATIONS[ value % 12]) {
                        addNode(pitchNode, "alter", (ks <= 7 ? "1" : "-1"));
                    }

                    Node technicalNode = addNode(addNode(noteNode, "notations"), "technical");
                    addNode(technicalNode, "fret", Integer.toString(note.getValue()));
                    addNode(technicalNode, "string", Integer.toString(note.getString()));

                    addNode(noteNode, "voice", "1");
                    writeDuration(noteNode, voice.getDuration());

                    if (note.isTiedNote()) {
                        addAttribute(addNode(noteNode, "tie"), "type", "stop");
                    }
                    if (n > 0) {
                        addNode(noteNode, "chord");
                    }
                }
            }
        }
    }

    private void writeDuration(Node parent, TGDuration duration) {
        int index = duration.getIndex();
        if (index >= 0 && index <= 6) {
            int value = (DURATION_VALUES[ index] * duration.getDivision().getTimes() / duration.getDivision().getEnters());
            if (duration.isDotted()) {
                value += (value / 2);
            } else if (duration.isDoubleDotted()) {
                value += ((value / 4) * 3);
            }

            addNode(parent, "duration", Integer.toString(value));
            addNode(parent, "type", DURATION_NAMES[ index]);

            if (duration.isDotted()) {
                addNode(parent, "dot");
            } else if (duration.isDoubleDotted()) {
                addNode(parent, "dot");
                addNode(parent, "dot");
            }

            if (!duration.getDivision().isEqual(TGDivisionType.NORMAL)) {
                Node divisionType = addNode(parent, "time-modification");
                addNode(divisionType, "actual-notes", Integer.toString(duration.getDivision().getEnters()));
                addNode(divisionType, "normal-notes", Integer.toString(duration.getDivision().getTimes()));
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
            return builder.newDocument();
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
            idTransform.transform(input, output);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
