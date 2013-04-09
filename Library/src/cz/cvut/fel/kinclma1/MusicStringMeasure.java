package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.exceptions.IncompatibleTrackException;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * Measure of a MusicStringTrack
 */
class MusicStringMeasure {
    private int tempo;
    private TempoTracker tempoTracker;
    private List<MusicStringBeat> beats;

    /**
     * Creates a MusicStringMeasure from a TGMeasure
     * @param tgMeasure source measure
     * @param tempoTracker the track's TempoTracker
     * @param drumTrack flag signalizing whether a drum track is processed
     */
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

    /**
     * Create a MusicStringMeasure from a list of strings - music string split along spaces
     * @param measure Music string measure representation split on spaces
     * @param tempoTracker the track's TempoTracker
     * @param drumTrack flag signalizing whether a drum track is processed
     */
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

    /**
     * Constructor used only when adding a new track to the song
     * @param measure source measure
     * @param tempoTracker the track's tempo tracker
     * @param refMeasure a reference measure from another track to check new measure's length
     * @throws IncompatibleTrackException when measures are not of the same length
     */
    MusicStringMeasure(List<String> measure, TempoTracker tempoTracker, MusicStringMeasure refMeasure)
            throws IncompatibleTrackException {
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

    /**
     * Returns the sum of proportional durations of the beats
     * @return sum of proportional durations of the beats
     */
    int getDuration() {
        int measureLength = 0;
        for (MusicStringBeat beat : beats) {
            measureLength += beat.getProportionalDuration();
        }
        return measureLength;
    }

    /**
     * Returns a list of beats - package-private for a good reason
     * @return list of beats
     */
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

    /**
     * Returns a TGMeasure equivalent of this measure
     * @param factory universal TuxGuitar factory
     * @param track track to which the measure should be added
     * @return TGMeasure equivalent of this measure
     */
    public TGMeasure toTGMeasure(TGFactory factory, TGTrack track) {
        TGMeasure measure = factory.newMeasure(factory.newHeader());
        measure.getHeader().getTempo().setValue(tempo);
        measure.setTrack(track);
        int measureLength = 0;
        for (MusicStringBeat beat : beats) {
            measure.addBeat(beat.toTGBeat(factory, measure));
            measureLength += beat.getProportionalDuration();
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

    /**
     * Returns the updated source array of the lowest tone and maximum simultaneously played tones
     * used to create strings for the TGTrack
     * @param ltms source array of the lowest tone and maximum simultaneously played tones
     * @return updated source array of the lowest tone and maximum simultaneously played tones
     */
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

    /**
     * Returns the shortest possible note in this measure - resolution
     * @return shortest possible note in this measure - resolution
     */
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
            beatsDuration += beat.getProportionalDuration();
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
