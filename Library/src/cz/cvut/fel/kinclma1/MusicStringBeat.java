package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 14.9.12
 * Time: 23:20
 * To change this template use File | Settings | File Templates.
 */
class MusicStringBeat {
    private List<MusicStringNote> notes;
    private MusicStringRest rest;

    public MusicStringBeat(TGBeat tgBeat, boolean drumTrack) {
        if(tgBeat.isRestBeat()) {
            for(int i = 0; i < tgBeat.countVoices(); i ++) {
                if (tgBeat.getVoice(i).isRestVoice()) {
                    rest = new MusicStringRest(tgBeat.getVoice(i));
                    break;
                }
            }
        } else {
            TGBeat beat = joinVoices(tgBeat);
            int numNotes = beat.getVoice(0).countNotes();
            notes = new ArrayList<MusicStringNote>(numNotes);
            for (int i = 0; i < numNotes; i ++) {
                notes.add(new MusicStringNote(beat.getVoice(0).getNote(i),drumTrack));
            }
        }
    }

    public MusicStringBeat(String strBeat, boolean drumTrack) {
        if (strBeat.charAt(0) == 'R') {
            rest = new MusicStringRest(strBeat);
        } else {
            String[] noteArr = strBeat.split("\\+");
            notes = new ArrayList<MusicStringNote>(noteArr.length);
            for (String note : noteArr) {
                notes.add(new MusicStringNote(note, drumTrack));
            }
        }
    }

    private TGBeat joinVoices(TGBeat tgBeat) {
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

        voices.get(0).setDuration(minDuration);
        for (i = 1; i < voices.size(); i ++) {
            TGVoice voice = voices.get(i);
            for (int j = 0; j < voice.countNotes(); j ++) {
                voices.get(0).addNote(voice.getNote(j));
            }
            voice.setEmpty(true);
        }

        tgBeat.setVoice(0, voices.get(0));
        return tgBeat;
    }

    @Override
    public String toString() {
        if(notes != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < notes.size(); i ++) {
                sb.append(notes.get(i).toString());
                if (i < notes.size() - 1) {
                    sb.append("+");
                }
            }
            return sb.toString();
        } else {
            return rest.toString();
        }
    }

    public TGBeat toTGBeat(TGFactory factory, TGMeasure measure) {
        TGBeat beat = factory.newBeat();
        if (rest != null) {
            beat.setVoice(0, rest.toTGVoice(beat.getVoice(0)));
        } else {
            TGVoice voice = beat.getVoice(0);
            notes.get(0).configureTGVoice(voice);
//            for (int a = 1; a < beat.countVoices(); a ++) {
//                beat.getVoice(a).setEmpty(true);
//            }
            int i = 1;
            List<TGString> strings = measure.getTrack().getStrings();
            List<MusicStringNote> newNotes = new ArrayList<MusicStringNote>(notes);
            Collections.sort(newNotes, Collections.reverseOrder());
            for (MusicStringNote note : newNotes) {
                while (i < strings.size() && note.value() < strings.get(i - 1).getValue()) {
                    i ++;
                }
                TGNote tgNote = note.toTGNote(factory);
                tgNote.setValue(tgNote.getValue() - strings.get(i - 1).getValue());
                tgNote.setString(i++);
                voice.addNote(tgNote);
            }
            beat.setMeasure(measure);
        }
        return beat;
    }

    int getDurationDiv128() {
        return rest != null ? rest.getDurationDiv128() : notes.get(0).getDurationDiv128();
    }

    int getLowestTone() {
        int lowest = Integer.MAX_VALUE;
        if (notes == null) {
            return lowest;
        }
        int current;
        for (MusicStringNote note : notes) {
            current = note.value();
            if (current < lowest) {
                lowest = current;
            }
        }
        return lowest;
    }

    int countTones() {
        return notes == null ? 0 : notes.size();
    }
}
