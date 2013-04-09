package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.tonefilters.*;

import java.util.*;

/**
 * Abstract class implementing all methods needed by any InstrumentTones filter class
 */
public abstract class AbstractTones implements InstrumentTones {

    private Map<String, Set<MusicStringTone>> tones;

    /**
     * Creates tone filter from an array of MusicStringTone string representations
     * @param toneStrings array of MusicStringTone string representations
     */
    protected AbstractTones(String[] toneStrings) {
        initToneMap();
        setTones(toneStrings);
    }

    /**
     * Creates tone filter from a list of MusicStringTones
     * @param toneList list of tones to be used by the filter
     */
    protected AbstractTones(List<MusicStringTone> toneList) {
        initToneMap();
        setToneList(toneList);
    }

    /**
     * Creates a tone filter using a range of MusicStringTones from min to max
     * @param min Lowest filter tone
     * @param max Highest filter tone
     */
    protected AbstractTones(MusicStringTone min, MusicStringTone max) {
        initToneMap();
        setRange(min,max);
    }

    private void initToneMap() {
        MusicStringTone.RelativeTone[] relativeTones = MusicStringTone.RelativeTone.values();
        tones = new HashMap<String, Set<MusicStringTone>>(12);
        for (int i = 0; i < relativeTones.length; i++) {
            tones.put(relativeTones[i].toString(), new HashSet<MusicStringTone>());
        }
    }

    private void setTones(String[] toneStrings) {
        for (int i = 0; i < toneStrings.length; i ++) {
            MusicStringTone tone = new MusicStringTone(toneStrings[i]);
            tones.get(tone.relativeTone()).add(tone);
        }
    }

    private void setToneList(List<MusicStringTone> toneList) {
        for (MusicStringTone tone : toneList) {
            tones.get(tone.relativeTone()).add(tone);
        }
    }

    private void setRange(MusicStringTone min, MusicStringTone max) {
        int minInt = min.toInt();
        int maxInt = max.toInt();
        MusicStringTone tone;
        for (int i = minInt; i <= maxInt; i++) {
            tone = new MusicStringTone(i);
            tones.get(tone.relativeTone()).add(tone);
        }
    }

    /**
     * Returns a FlatTrack containing the intersection of original tones and filter tones in all octaves of the filter
     * @param orig Original flat track containing tones without octave numbers
     * @return New flat track containing the intersection of original tones and filter tones in all octaves of the filter
     */
    @Override
    public FlatTrack filterTones(FlatTrack orig) {

        Iterator<HashSet<String>> iterator = orig.getIterator();
        FlatTrack newTrack = new FlatTrack(orig.getDuration());

        while (iterator.hasNext()) {
            newTrack.addToneSet(fillNewBeat(iterator.next()));
        }

        return newTrack;
    }

    private HashSet<String> fillNewBeat(HashSet<String> oldBeat) {
        HashSet<String> newBeat = new HashSet<String>();
        for (String s : oldBeat) {
            supplyOctaves(s, newBeat);
        }
        if (newBeat.isEmpty()) {
            newBeat.addAll(oldBeat);
        }
        return newBeat;
    }

    private void supplyOctaves(String sourceTone, HashSet<String> beat) {
        Set<MusicStringTone> octaves = tones.get(sourceTone);
        if (octaves != null) {
            for (MusicStringTone tone : octaves) {
                beat.add(tone.toString());
            }
        }
    }
}
