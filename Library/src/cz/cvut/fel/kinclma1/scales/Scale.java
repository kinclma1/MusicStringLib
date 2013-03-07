package cz.cvut.fel.kinclma1.scales;

import cz.cvut.fel.kinclma1.MusicStringTone;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 6.3.13
 * Time: 22:52
 * To change this template use File | Settings | File Templates.
 */
public abstract class Scale {
    protected final String[] tones;
    protected int[] definingTones;
    protected int[] scaleTones;

    protected Scale() {
        tones = new String[]{"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
    }

    public Set<String> getMatch(Set<String> played) {
        Set<String> scale = null;
        for (int i = 0; i < tones.length && scale == null; i ++) {
            if (played.contains(tones[i])) {
                scale = getSingleMatch(i, played);
            }
        }
        return scale;
    }

    private Set<String> getSingleMatch(int baseTone, Set<String> played) {
        Set<String> def = createSet(baseTone, definingTones);
        if (played.containsAll(def)) {
            return createSet(baseTone, scaleTones);
        }
        return null;
    }

    private Set<String> createSet(int baseTone, int[] indices) {
        Set<String> set = new HashSet<String>(indices.length);
        for (int i = 0; i < indices.length; i ++) {
            set.add(getTone(indices[i] + baseTone));
        }
        return set;
    }

    private String getTone(int index) {
        return tones[index % tones.length];
    }
}
