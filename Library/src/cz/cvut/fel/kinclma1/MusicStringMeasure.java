package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 14.9.12
 * Time: 23:20
 * To change this template use File | Settings | File Templates.
 */
public class MusicStringMeasure {
    private int tempo;
    private MusicStringTrack.TempoTracker tempoTracker;
    private List<MusicStringBeat> beats;

    public MusicStringMeasure(TGMeasure tgMeasure, MusicStringTrack.TempoTracker tempoTracker, boolean drumTrack) {
        tempo = tgMeasure.getTempo().getValue();
        this.tempoTracker = tempoTracker;
        int numBeats = tgMeasure.countBeats();
        beats = new ArrayList<MusicStringBeat>(numBeats);
        for (TGBeat tgBeat : (List<TGBeat>)tgMeasure.getBeats()) {
            beats.add(new MusicStringBeat(tgBeat, drumTrack));
        }
    }

    public MusicStringMeasure(List<String> measure, MusicStringTrack.TempoTracker tempoTracker, boolean drumTrack) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(tempoIndicator());
        for (MusicStringBeat beat : beats) {
            sb.append(beat.toString());
            sb.append(" ");
        }
        return sb.toString();
    }

    public TGMeasure toTGMeasure(TGFactory factory) {
        TGMeasure measure = factory.newMeasure(factory.newHeader());
        measure.getHeader().getTempo().setValue(tempo);
        for (MusicStringBeat beat : beats) {
            measure.addBeat(beat.toTGBeat(factory));
        }

        return measure;
    }

    private String tempoIndicator() {
        if(!tempoTracker.changed(tempo)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(3);
        sb.append("T");
        sb.append(Integer.toString(tempo));
        sb.append(" ");
        return sb.toString();
    }
}
