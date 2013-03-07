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

    protected Map<String, Set<MusicStringTone>> tones;
    protected boolean tonesEmpty;

    protected InstrumentTones() {
        initToneMap();
    }

    public FlatTrack filterTones(FlatTrack orig) {
        FlatTrack newTrack;
        if (tonesEmpty) {
            return orig;
        }
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

        return newTrack;
    }

    protected final void setToneList(List<MusicStringTone> toneList) {
        for (MusicStringTone tone : toneList) {
            tones.get(tone.relativeTone()).add(tone);
        }
        tonesEmpty = false;
    }

    protected final void setRange(MusicStringTone min, MusicStringTone max) {
        int minInt = min.toInt();
        int maxInt = max.toInt();
        MusicStringTone tone;
        for (int i = minInt; i <= maxInt; i++) {
            tone = new MusicStringTone(i);
            tones.get(tone.relativeTone()).add(tone);
        }
        tonesEmpty = false;
    }

    private void initToneMap() {
        MusicStringTone.RelativeTone[] relativeTones = MusicStringTone.RelativeTone.values();
        tones = new HashMap<String, Set<MusicStringTone>>(12);
        for (int i = 0; i < relativeTones.length; i++) {
            tones.put(relativeTones[i].toString(), new HashSet<MusicStringTone>());
        }
        tonesEmpty = true;
    }
}
