
package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.io.FileExporter;
import cz.cvut.fel.kinclma1.player.MusicStringPlayer;
import cz.cvut.fel.kinclma1.player.PlayerListener;
import cz.cvut.fel.kinclma1.tonefilters.TestTones;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.song.models.TGSong;
import org.jfugue.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author void
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MusicStringSong ms = null;
        try {
            ms = MusicStringSong.create("/home/void/project/rem1.gp4");
            System.out.println(ms);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TGFileFormatException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println(ms.getPossibleNotes(new TestTones()));
//        MusicStringPlayer player = new MusicStringPlayer(Instrument.DISTORTION_GUITAR.toMusicString() +
//                        ms.getPossibleNotes(new TestTones()).toString(),
//                new TestListener1());
//        String[] bu = MusicStringSong.getExportFormats();
//        for (int i = 0; i < bu.length; i ++) {
//            System.out.println(bu[i]);
//        }
//        bu = MusicStringSong.getInputFormats();
//        for (int i = 0; i < bu.length; i ++) {
//            System.out.println(bu[i]);
//        }
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
}
