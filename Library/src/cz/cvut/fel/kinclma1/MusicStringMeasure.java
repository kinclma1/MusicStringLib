package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 14.9.12
 * Time: 23:20
 * To change this template use File | Settings | File Templates.
 */
class MusicStringMeasure {
    private int tempo;
    private TempoTracker tempoTracker;
    private List<MusicStringBeat> beats;

    public MusicStringMeasure(TGMeasure tgMeasure, TempoTracker tempoTracker, boolean drumTrack) {
        tempo = tgMeasure.getTempo().getValue();
        this.tempoTracker = tempoTracker;
        int numBeats = tgMeasure.countBeats();
        beats = new ArrayList<MusicStringBeat>(numBeats);
        for (TGBeat tgBeat : tgMeasure.getBeats()) {
            beats.add(new MusicStringBeat(tgBeat, drumTrack));
        }
        supplyRests(tgMeasure.getTimeSignature(), drumTrack);
    }

    public MusicStringMeasure(List<String> measure, TempoTracker tempoTracker, boolean drumTrack) {
        this.tempoTracker = tempoTracker;
        int i = 0;
        if (measure.get(0).charAt(0) == 'T') {
            tempo = Integer.parseInt(measure.get(0).substring(1));
            tempoTracker.changed(tempo);
            i ++;
        } else {
            tempo = tempoTracker.getTempo();
        }
        beats = new ArrayList<MusicStringBeat>(measure.size());
        for (; i < measure.size(); i ++) {
            beats.add(new MusicStringBeat(measure.get(i), drumTrack));
        }
    }

    MusicStringMeasure(List<String> measure, TempoTracker tempoTracker, MusicStringMeasure refMeasure)
            throws IncompatibleTrackException{
        this.tempoTracker = tempoTracker;
        tempo = refMeasure.tempo;
        beats = new ArrayList<MusicStringBeat>(measure.size());
        for (String beat : measure) {
            beats.add(new MusicStringBeat(beat, false));
        }
        if (getDuration() != refMeasure.getDuration()) {
            throw new IncompatibleTrackException(
                    "New track measure lengths do not match original tracks' measure lengths");
        }
    }

    int getDuration() {
        int measureLength = 0;
        for (MusicStringBeat beat : beats) {
            measureLength += beat.getDurationDiv128();
        }
        return measureLength;
    }

    List<MusicStringBeat> getBeats() {
        return beats;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(tempoIndicator());
        for (MusicStringBeat beat : beats) {
            sb.append(beat.toString());
            sb.append(' ');
        }
        return sb.toString();
    }

    public TGMeasure toTGMeasure(TGFactory factory, TGTrack track) {
        TGMeasure measure = factory.newMeasure(factory.newHeader());
        measure.getHeader().getTempo().setValue(tempo);
        measure.setTrack(track);
        int measureLength = 0;
        for (MusicStringBeat beat : beats) {
            measure.addBeat(beat.toTGBeat(factory, measure));
            measureLength += beat.getDurationDiv128();
        }

        measure.getHeader().setTimeSignature(getTimeSignature(factory, measureLength));
        return measure;
    }

    private TGTimeSignature getTimeSignature(TGFactory factory, int measureLength) {
        TGTimeSignature ts = factory.newTimeSignature();
        for (int i = 32; i >= 1; i /= 2) {
            if(measureLength % i == 0) {
                ts.setNumerator(measureLength / i);
                ts.getDenominator().setValue(128 / i);
                break;
            }
        }

        return ts;
    }

    int[] getLowestToneAndMaxTones(int[] ltms) {
        int tone;
        int max;
        for (MusicStringBeat beat : beats) {
            tone = beat.getLowestTone();
            max = beat.countTones();
            if (tone < ltms[0]) {
                ltms[0] = tone;
            }
            if (max > ltms[1]) {
                ltms[1] = max;
            }
        }
        return ltms;
    }

    MusicStringDuration getShortestNote() {
        MusicStringDuration shortest = new MusicStringDuration(Duration.WHOLE);
        for (MusicStringBeat beat : beats) {
            MusicStringDuration beatShortest = beat.getShortestNote();
            if (beatShortest.toInteger() > shortest.toInteger()) {
                shortest = beatShortest;
            }
        }
        return shortest;
    }

    private String tempoIndicator() {
        return tempoTracker.changed(tempo) ? String.format("T%d ", tempo) : "";
    }

    private void supplyRests(TGTimeSignature ts, boolean drumTrack) {
        int beatsDuration = 0;
        for (MusicStringBeat beat : beats) {
            beatsDuration += beat.getDurationDiv128();
        }
        int measureDuration = (128 / ts.getDenominator().getValue()) * ts.getNumerator();
        int remaining = measureDuration - beatsDuration;
        if (remaining > 0) {
            for (Duration d : Duration.values()) {
                while (128 / d.toInteger() <= remaining) {
                    beats.add(new MusicStringBeat('R' + d.toString(), drumTrack));
                    remaining -= 128 / d.toInteger();
                }
            }
        }
    }
}
