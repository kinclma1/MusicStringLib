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

    private class RepetitionTracker {
        private LinkedList<TGMeasure> measures;
        private boolean repeatOpen;
        private boolean alternateEnding;

        RepetitionTracker() {
            measures = new LinkedList<TGMeasure>();
            repeatOpen = true;
            alternateEnding = false;
        }

        void processMeasure(TGMeasure measure) {
            if (!repeatOpen && measure.getHeader().getRepeatAlternative() > 1) {
                addMeasures(measures);
                alternateEnding = true;
            }
            addMeasure(measure);
            if(measure.isRepeatOpen()) {
                repeatOpen = true;
                measures.clear();
                measures.add(measure);
            } else if (repeatOpen && measure.getHeader().getRepeatAlternative() > 0) {
                alternateEnding = true;
                repeatOpen = false;
            } else if (!alternateEnding && measure.getRepeatClose() > 0) {
                measures.add(measure);
                repeatOpen = false;
                addMeasure(measure);
                for (int i = 0; i < measure.getRepeatClose(); i ++) {
                    addMeasures(measures);
                }
            } else if (alternateEnding && measure.getRepeatClose() > 0) {
                alternateEnding = false;
            } else if (repeatOpen) {
                measures.add(measure);
            }
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
        RepetitionTracker repetitionTracker = new RepetitionTracker();
        channel = tgTrack.getChannel().getChannel();
        drumTrack = channel == 9;
        instrument = Instrument.fromInt(tgTrack.getChannel().getInstrument());
        id = buildId();
        metaInfo = buildMeta();
        this.measures = new ArrayList<MusicStringMeasure>(tgTrack.countMeasures());
        Iterator<TGMeasure> tgMeasureIterator = tgTrack.getMeasures();
        TGMeasure tgMeasure;
        while (tgMeasureIterator.hasNext()) {
            tgMeasure = tgMeasureIterator.next();
            repetitionTracker.processMeasure(tgMeasure);
        }
    }

    private void addMeasure(TGMeasure measure) {
        measures.add(new MusicStringMeasure(measure,tempoTracker,drumTrack));
    }

    private void addMeasures(List<TGMeasure> measureList) {
        for (TGMeasure measure : measureList) {
            addMeasure(measure);
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
            track.setStrings(initStrings(factory));
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
            TGMeasure tgMeasure = measure.toTGMeasure(factory, track);
            if (track.stringCount() < 6) {
                tgMeasure.setClef(2);
            }
            track.addMeasure(tgMeasure);
        }
        track.getColor().setR(255);
        track.setName(instrument.toString());
        return track;
    }

    private int[] getLowestToneAndMaxTones() {
        int[] ltms = new int[2];
        ltms[0] = Integer.MAX_VALUE;
        for (MusicStringMeasure measure : measures) {
            ltms = measure.getLowestToneAndMaxTones(ltms);
        }
        return ltms;
    }

    private ArrayList<TGString> initStrings(TGFactory factory) {
        int[] ltms = getLowestToneAndMaxTones();
        if (ltms[0] >= new MusicStringTone("E3").toInteger() && ltms[1] <= 6) {
            return createStrings(factory, new String[]{"E5", "B4", "G4", "D4", "A3", "E3"});
        } else if (ltms[0] >= new MusicStringTone("E2").toInteger() && ltms[1] < 4) {
            return createStrings(factory, new String[]{"G3", "D3", "A2", "E2"});
        } else if (ltms[0] >= new MusicStringTone("B2").toInteger() && ltms[1] <= 7) {
            return createStrings(factory, new String[]{"E5", "B4", "G4", "D4", "A3", "E3", "B2"});
        } else if (ltms[0] >= new MusicStringTone("B1").toInteger() && ltms[1] < 4) {
            return createStrings(factory, new String[]{"G3", "D3", "A2", "E2", "B1"});
        } else {
            return createStrings(factory, new String[]{"C0", "C0", "C0", "C0", "C0", "C0", "C0"});
        }
    }

    private ArrayList<TGString> createStrings(TGFactory factory, String[] tones) {
        ArrayList<TGString> stringList = new ArrayList<TGString>(tones.length);
        TGString string;
        for (int i = 0; i < tones.length; i ++) {
            string = factory.newString();
            string.setNumber(i + 1);
            string.setValue(new MusicStringTone(tones[i]).toInteger());
            stringList.add(string);
        }
        return stringList;
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
