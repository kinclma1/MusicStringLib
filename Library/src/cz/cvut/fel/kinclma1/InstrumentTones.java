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
            //todo consider flat track tones represented as numbers
            Iterator<HashSet<String>> iterator = orig.getIterator();
            newTrack = new FlatTrack(orig.getDuration());
            HashSet<String> oldBeat;
            HashSet<String> newBeat;
            while (iterator.hasNext()) {
                oldBeat = iterator.next();
                newBeat = new HashSet<String>();
                for (String s : oldBeat) {
                    Set<MusicStringTone> octaves = tones.get(s);
                    if (octaves == null) {
                        newBeat.addAll(oldBeat);//todo tohle smrdi
                    }
                    for (MusicStringTone tone : octaves) {
                        newBeat.add(tone.toString());
                    }
                }
                newTrack.addToneSet(newBeat);
            }
        }
        return newTrack;
    }
}
