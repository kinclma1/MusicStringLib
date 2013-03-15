
package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.io.FileExporter;
import cz.cvut.fel.kinclma1.player.MusicStringPlayer;
import cz.cvut.fel.kinclma1.player.PlayerListener;
import cz.cvut.fel.kinclma1.tonefilters.SoloTones;
import cz.cvut.fel.kinclma1.tonefilters.TestTones;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.song.models.TGSong;
import org.jfugue.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author void
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //todo Javadoc
        MusicStringSong ms = null;
        try {
            ms = MusicStringSong.create("/home/void/project/emoll.tg");
            System.out.println(ms);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TGFileFormatException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        ms.addTrack(randomTones(ms), Instrument.DISTORTION_GUITAR);
        MusicStringPlayer player = new MusicStringPlayer(ms.toString(),
                new TestListener1());
        try {
            ms.export("/home/void/project/newemoll.tg");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TGFileFormatException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
//        try {
//            ms.export("/home/void/project/rem.musicstring");
//            MusicStringSong ms1 = MusicStringSong.create("/home/void/project/rem.musicstring");
//            ms1.export("/home/void/project/newrem.tg");
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (TGFileFormatException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        TestListener1 listener = new TestListener1();
//        MusicStringPlayer player = new MusicStringPlayer(ms.toString(), listener);
//        player.play();
//        TGSong tgs = ms.toTGSong();
//        new FileExporter().exportSong(tgs, "/home/void/project/newrem1.tg");
    }

    private static String randomTones(MusicStringSong ms) {
        FlatTrack ft = ms.getPossibleNotes(new SoloTones());
        Iterator<HashSet<String>> it = ft.getIterator();
        while (it.hasNext()) {
            HashSet<String> beat = it.next();
            Iterator<String> bi = beat.iterator();
            String s = "";
            for (int i = 0; i < Math.random() * beat.size(); i ++) {
                s = bi.next();
            }
            beat.clear();
            beat.add(s);
        }
        return ft.toString();
    }
}
