package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 13.9.12
 * Time: 21:10
 * To change this template use File | Settings | File Templates.
 */
public class MusicStringTrack {

    class TempoTracker {
        private int tempo = 0;

        boolean changed(int newTempo) {
            if(newTempo != tempo) {
                tempo = newTempo;
                return true;
            }
            return false;
        }

        int getTempo() {
            return tempo;
        }
    }

    private String id;
    private String metaInfo;

    private int channel;
    private Instrument instrument;
    private boolean drumTrack;
    private TempoTracker tempoTracker;
    private List<MusicStringMeasure> measures;

    public MusicStringTrack(TGTrack tgTrack) {
        tempoTracker = new TempoTracker();
        channel = tgTrack.getChannel().getChannel();
        drumTrack = channel == 9;
        instrument = Instrument.fromInt(tgTrack.getChannel().getInstrument());
        id = buildId();
        metaInfo = buildMeta();
        this.measures = new ArrayList<MusicStringMeasure>(tgTrack.countMeasures());
        Iterator<TGMeasure> tgMeasureIterator = tgTrack.getMeasures();
        while (tgMeasureIterator.hasNext()) {
            measures.add(new MusicStringMeasure(tgMeasureIterator.next(),tempoTracker,drumTrack));
        }
    }

    public MusicStringTrack(String msTrack) {
        tempoTracker = new TempoTracker();
        StringTokenizer tokenizer = new StringTokenizer(msTrack);
        String token;
        measures = new ArrayList<MusicStringMeasure>();
        ArrayList<String> measure = new ArrayList<String>();
        while(tokenizer.hasMoreElements()) {
            token = tokenizer.nextToken();
            if(token.charAt(0) == 'V') {
                channel = Integer.parseInt(token.substring(1));
                drumTrack = channel == 9;
            } else if(token.charAt(0) == 'I') {
                instrument = Instrument.valueOf(token.substring(2,token.length() - 1));
            } else if (token.charAt(0) != '|') {
                measure.add(token);
            } else {
                measures.add(new MusicStringMeasure(measure, tempoTracker, drumTrack));
                measure = new ArrayList<String>();
            }
        }
        if (!measure.isEmpty()) {
            measures.add(new MusicStringMeasure(measure, tempoTracker, drumTrack));
        }
        metaInfo = buildMeta();
        id = buildId();
    }

    public String getMetaInfo() {
        return metaInfo;
    }

    public String getId() {
        return id;
    }

    public List<MusicStringMeasure> getMeasures() {
        return new ArrayList<MusicStringMeasure>(measures);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(metaInfo);
        for(MusicStringMeasure measure : measures) {
            sb.append(measure.toString());
            sb.append("| ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    public TGTrack toTGTrack(TGFactory factory) {
        TGTrack track = factory.newTrack();
        TGChannel tgChannel = track.getChannel();
        tgChannel.setChannel((short) channel);
        if (channel != 9) {
            tgChannel.setInstrument((short) instrument.toInteger());
            int strings = 6;
            ArrayList<TGString> stringList = new ArrayList<TGString>(strings);
            for (int i = 0; i < strings; i ++) {
                TGString string = factory.newString();
                string.setNumber(i + 1);
                string.setValue(0);
                stringList.add(string);
            }
            track.setStrings(stringList);
            //TODO no strings in longer tracks
        } else {
            ArrayList<TGString> stringList = new ArrayList<TGString>(6);
            for (int i = 0; i < 6; i ++) {
                TGString string = factory.newString();
                string.setNumber(i + 1);
                stringList.add(string);
            }
            track.setStrings(stringList);
        }
        for (MusicStringMeasure measure : measures) {
            track.addMeasure(measure.toTGMeasure(factory, track));
        }

        track.setLyrics(factory.newLyric());
        track.setMute(false);
        track.setNumber(new Random().nextInt());
        track.setColor(factory.newColor());
        track.getColor().setR(0);
        track.getColor().setB(0);
        track.getColor().setG(0);

        track.setName("Track");
        return track;
    }

    private String buildId() {
        StringBuilder sb = new StringBuilder(2);
        if(!drumTrack) {
            sb.append(channelMusicString());
            sb.append(instrument.toString());
        } else {
            sb.append(channelMusicString());
            sb.append("DRUMS");
        }

        return sb.toString();
    }

    private String buildMeta() {
        if(!drumTrack) {
            StringBuilder sb = new StringBuilder(2);
            sb.append(channelMusicString());
            sb.append(instrument.toMusicString());
            return sb.toString();
        }
        return channelMusicString();
    }

    private String channelMusicString() {
        StringBuilder sb = new StringBuilder(3);
        sb.append("V");
        sb.append(Integer.toString(channel));
        sb.append(" ");
        return sb.toString();
    }
}
