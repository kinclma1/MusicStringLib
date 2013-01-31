
package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.io.FileExporter;
import cz.cvut.fel.kinclma1.player.MusicStringPlayer;
import org.herac.tuxguitar.song.models.TGSong;
import org.jfugue.Player;

import java.io.File;

/**
 *
 * @author void
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MusicStringSong ms = new MusicStringSong(new File("/home/void/project/rem.gp3"));
        ms.getPossibleNotes();
        TestListener listener = new TestListener();
        MusicStringPlayer player = new MusicStringPlayer(ms.toString(), listener);
        MusicStringPlayer player1 = new MusicStringPlayer(ms.toString(), listener);
        player.setPosition(10);
        player1.setPosition(100);
        player.play();
        player1.play();
        try {
            Thread.sleep(5000);
            player.pause();
            player1.pause();
            Thread.sleep(5000);
            player.setPosition(20);
            player1.setPosition(200);
            player.play();
            player1.play();
            Thread.sleep(5000);
            player.stop();
            player1.stop();
            Thread.sleep(5000);
            player.play();
            player1.play();
            Thread.sleep(5000);
            player.close();
            player1.setPosition(240);
            Thread.sleep(40000);
            player1.close();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        TGSong tgs = ms.toTGSong();
        new FileExporter().exportSong(tgs, "/home/void/project/newrem.tg");
    }
}
