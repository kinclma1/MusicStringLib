package cz.cvut.fel.kinclma1;

import org.herac.tuxguitar.song.models.TGVoice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 24.1.13
 * Time: 23:35
 * To change this template use File | Settings | File Templates.
 */
public class BeatContent {
    private List<BeatElement> content;

    public BeatContent(TGVoice voice, boolean drumTrack) {
        if (voice.isRestVoice()) {
            content = new ArrayList<BeatElement>(1);
            content.add(new MusicStringRest(voice));
        } else {
            int noteCount = voice.countNotes();
            content = new ArrayList<BeatElement>(noteCount);
            for (int i = 0; i < noteCount; i ++) {
                content.add(new MusicStringNote(voice.getNote(i), drumTrack));
            }
        }
    }
}
