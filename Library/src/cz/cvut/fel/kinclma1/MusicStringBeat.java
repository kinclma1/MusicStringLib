package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.*;

import java.util.*;

/**
 * Single beat of a MusicStringTrack
 */
class MusicStringBeat {

    private List<BeatElement> content;

    private MusicStringDuration duration;

    /**
     * Creates a MusicStringBeat from the given TGBeat
     * @param tgBeat Beat of the TGSong
     * @param drumTrack Flag to determine whether to create a beat of a drum track or an ordinary one
     * @throws UnsupportedOperationException when the input contains triplets
     */
    public MusicStringBeat(TGBeat tgBeat, boolean drumTrack) {
        TGVoice voice;
        if(tgBeat.isRestBeat()) {
            voice = getRestVoice(tgBeat);
            content = new ArrayList<BeatElement>(1);
            duration = new MusicStringDuration(voice.getDuration());
            content.add(new MusicStringRest(duration));
        } else {
            voice = joinToOneVoice(tgBeat);
            duration = new MusicStringDuration(voice.getDuration());
            int noteCount = voice.countNotes();
            content = new ArrayList<BeatElement>(noteCount);
            for (int i = 0; i < noteCount; i ++) {
                content.add(new MusicStringNote(voice.getNote(i), drumTrack, duration));
            }
        }
        if (!voice.getDuration().getDivision().isEqual(TGDivisionType.NORMAL)) {
            throw new UnsupportedOperationException("Input file contains triplets.");
        }
    }

    /**
     * Parses a music string beat
     * @param strBeat music string beat
     * @param drumTrack Flag to determine whether to parse a beat of a drum track or an ordinary one
     */
    public MusicStringBeat(String strBeat, boolean drumTrack) {
        duration = new MusicStringDuration(strBeat, 1);
        if (strBeat.charAt(0) == 'R') {
            content = new ArrayList<BeatElement>(1);
            content.add(new MusicStringRest(duration));
        } else {
            String[] noteArr = strBeat.split("\\+");
            content = new ArrayList<BeatElement>(noteArr.length);
            for (String note : noteArr) {
                content.add(new MusicStringNote(note, drumTrack, duration));
            }
        }
    }

    private TGVoice joinToOneVoice(TGBeat tgBeat) {
        List<TGVoice> voices = new LinkedList<TGVoice>();
        TGDuration minDuration = null;
        int i = 0;
        while (i < tgBeat.countVoices()) {
            TGVoice voice = tgBeat.getVoice(i);
            i ++;
            if (!voice.isEmpty()) {
                voices.add(voice);
            } else {
                continue;
            }
            minDuration = minDuration == null || voice.getDuration().getValue() > minDuration.getValue() ?
                    voice.getDuration() :
                    minDuration;
        }

        TGVoice mainVoice = voices.get(0);
        mainVoice.setDuration(minDuration);
        for (i = 1; i < voices.size(); i ++) {
            TGVoice voice = voices.get(i);
            for (int j = 0; j < voice.countNotes(); j ++) {
                mainVoice.addNote(voice.getNote(j));
            }
            voice.setEmpty(true);
        }

        return mainVoice;
    }

    private TGVoice getRestVoice(TGBeat tgBeat) {
        for(int i = 0; i < tgBeat.countVoices(); i ++) {
            TGVoice voice = tgBeat.getVoice(i);
            if (voice.isRestVoice()) {
                return voice;
            }
        }
        return null;
    }

    private BeatElement firstElement() {
        return content.get(0);
    }

    @Override
    public String toString() {
        int elements = content.size();
        StringBuilder sb = new StringBuilder(elements * 4);
        for (int i = 0; i < elements; i ++) {
            sb.append(content.get(i).toString());
            if (i < elements - 1) {
                sb.append('+');
            }
        }
        return sb.toString();
    }

    /**
     * Returns a TGBeat equivalent of this
     * @param factory Universal TuxGuitar factory
     * @param measure TGMeasure in which to add the beat
     * @return TGBeat equivalent of this
     */
    public TGBeat toTGBeat(TGFactory factory, TGMeasure measure) {
        TGBeat beat = factory.newBeat();
        TGVoice voice = beat.getVoice(0);
        handleTGVoice(voice);
        if (firstElement().value() >= 0) {
              setNotesToStrings(factory, measure, voice);
        }
        beat.setMeasure(measure);
        return beat;
    }

    private void handleTGVoice(TGVoice voice) {
        voice.getDuration().setValue(duration.toInteger());
        voice.getDuration().setDotted(duration.isDotted());
        voice.setEmpty(false);
    }

    private void setNotesToStrings(TGFactory factory, TGMeasure measure, TGVoice voice) {
        int i = 1;
        List<TGString> strings = measure.getTrack().getStrings();
        List<BeatElement> newNotes = new ArrayList<BeatElement>(content);
        Collections.sort(newNotes, Collections.reverseOrder());
        for (BeatElement note : newNotes) {
            while (i < strings.size() && note.value() < strings.get(i - 1).getValue()) {
                i ++;
            }
            TGNote tgNote = note.toTGNote(factory);
            tgNote.setValue(tgNote.getValue() - strings.get(i - 1).getValue());
            tgNote.setString(i++);
            voice.addNote(tgNote);
        }
    }

    /**
     * Returns proportional duration number
     * @return Proportional duration number
     */
    int getProportionalDuration() {
        return duration.toProportionalInt();
    }

    /**
     * Returns lowest tone in the beat int value
     * @return lowest tone in the beat int value
     */
    int getLowestTone() {
        int lowest = Integer.MAX_VALUE;
        int current;
        for (BeatElement element : content) {
            current = element.value();
            if (current >= 0 && current < lowest) {
                lowest = current;
            }
        }
        return lowest;
    }

    /**
     * Returns number of notes in the beat
     * @return number of notes in the beat
     */
    int countTones() {
        return content.size();
    }

    /**
     * Returns shortest duration - duration resolution
     * @return shortest duration - duration resolution
     */
    MusicStringDuration getShortestNote() {
        return duration.shortest();
    }

    /**
     * Returns a set of string representations of the beat elements
     * @return set of string representations of the beat elements
     */
    HashSet<String> getToneSet() {
        HashSet<String> toneSet = new HashSet<String>(content.size());
            for (BeatElement element : content) {
                toneSet.add(element.getTone());
            }
        return toneSet;
    }
}
