package cz.cvut.fel.kinclma1.tonefilters;

import cz.cvut.fel.kinclma1.InstrumentTones;
import cz.cvut.fel.kinclma1.MusicStringTone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 25.2.13
 * Time: 1:07
 * To change this template use File | Settings | File Templates.
 */
public class HarmonicaATones extends InstrumentTones {
    public HarmonicaATones() {
        List<MusicStringTone> toneList = new ArrayList<MusicStringTone>(20);
        String[] toneArray = new String[]{"A3","C#4","E4","A4","C#5","E5","A5","C#6","E6","A6",
                "B3","E4","G#4","B4","D5","F#5","G#5","B5","D6","F#6"};
        for (int i = 0; i < toneArray.length; i ++) {
            toneList.add(new MusicStringTone(toneArray[i]));
        }
        super.setToneList(toneList);
    }
}
