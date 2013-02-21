package cz.cvut.fel.kinclma1;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 31.1.13
 * Time: 21:10
 * To change this template use File | Settings | File Templates.
 */
public class InstrumentTones {
    public Map<String,Set<MusicStringTone>> tones = null;

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
                    for (MusicStringTone tone : octaves) {
                        newBeat.add(tone.toString());
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

    protected final void setRange(MusicStringTone min, MusicStringTone max) {
        int minInt = min.toInt();
        int maxInt = max.toInt();
        initToneMap();
        MusicStringTone tone;
        for (int i = minInt; i <= maxInt; i ++) {
            tone = new MusicStringTone(i);
            tones.get(tone.relativeTone()).add(tone);
        }
    }

    private void initToneMap() {
        MusicStringTone.RelativeTone[] relativeTones = MusicStringTone.RelativeTone.values();
        for (int i = 0; i < relativeTones.length; i ++) {
            tones.put(relativeTones[i].toString(), new HashSet<MusicStringTone>());
        }
    }
}
