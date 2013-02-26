package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.tonefilters.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 31.1.13
 * Time: 21:10
 * To change this template use File | Settings | File Templates.
 */
public class InstrumentTones {
    public static enum Instruments {
        GUITAR, GUITAR_7, BASS, BASS_5, HARMONICA_C, HARMONICA_A, TEST;
    }

    protected Map<String,Set<MusicStringTone>> tones = null;

    protected InstrumentTones() {
        initToneMap();
    }

    public static InstrumentTones create(Instruments filter) {
        switch (filter) {
            case GUITAR:
                return new GuitarTones();
            case GUITAR_7:
                return new Guitar7Tones();
            case BASS:
                return new BassTones();
            case BASS_5:
                return new Bass5Tones();
            case HARMONICA_C:
                return new HarmonicaCTones();
            case HARMONICA_A:
                return new HarmonicaATones();
            case TEST:
                return new TestTones();
        }
        return null;
    }

    public FlatTrack filterTones(FlatTrack orig) {
        FlatTrack newTrack;
        if (tones == null) {
            return orig;
        } else {
            Iterator<HashSet<String>> iterator = orig.getIterator();
            newTrack = new FlatTrack(orig.getDuration());
            HashSet<String> oldBeat;
            HashSet<String> newBeat;
            while (iterator.hasNext()) {
                oldBeat = iterator.next();
                newBeat = new HashSet<String>();
                for (String s : oldBeat) {
                    Set<MusicStringTone> octaves = tones.get(s);
                    if (octaves != null) {
                        for (MusicStringTone tone : octaves) {
                            newBeat.add(tone.toString());
                        }
                    }
                }
                if (newBeat.isEmpty()) {
                    newBeat.addAll(oldBeat);
                }
                newTrack.addToneSet(newBeat);
            }
        }
        return newTrack;
    }

    protected final void setToneList(List<MusicStringTone> toneList) {
        for (MusicStringTone tone : toneList) {
            tones.get(tone.relativeTone()).add(tone);
        }
    }

    protected final void setRange(MusicStringTone min, MusicStringTone max) {
        int minInt = min.toInt();
        int maxInt = max.toInt();
        MusicStringTone tone;
        for (int i = minInt; i <= maxInt; i ++) {
            tone = new MusicStringTone(i);
            tones.get(tone.relativeTone()).add(tone);
        }
    }

    private void initToneMap() {
        MusicStringTone.RelativeTone[] relativeTones = MusicStringTone.RelativeTone.values();
        tones = new HashMap<String, Set<MusicStringTone>>(12);
        for (int i = 0; i < relativeTones.length; i ++) {
            tones.put(relativeTones[i].toString(), new HashSet<MusicStringTone>());
        }
    }
}
