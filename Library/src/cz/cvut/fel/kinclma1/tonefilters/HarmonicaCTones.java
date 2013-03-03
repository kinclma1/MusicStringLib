package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.InstrumentTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 25.2.13
 * Time: 0:56
 * To change this template use File | Settings | File Templates.
 */
public class HarmonicaCTones extends InstrumentTones {

    public HarmonicaCTones() {
        List<MusicStringTone> toneList = new ArrayList<MusicStringTone>(20);
        String[] toneArray = new String[]{"C4","E4","G4","C5","E5","G5","C6","E6","G6","C7",
                "D4","G4","B4","D5","F5","A5","B5","D6","F6","A6"};
        for (int i = 0; i < toneArray.length; i ++) {
            toneList.add(new MusicStringTone(toneArray[i]));
        }
        setToneList(toneList);
    }
}
