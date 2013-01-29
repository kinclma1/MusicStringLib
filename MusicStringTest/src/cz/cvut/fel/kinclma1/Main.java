
package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.io.FileExporter;
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
        Player player = new Player();
        player.play(ms.toString());
        try {
            Thread.sleep(100000);
            player.stop();
            player.close();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        TGSong tgs = ms.toTGSong();
        new FileExporter().exportSong(tgs, "/home/void/project/newrem.tg");

//        try {
//            new  MusicXMLReader(new TGFactory(), new BufferedInputStream(new FileInputStream("/home/void/project/newmayhem-short.xml"))).readSong();
//        } catch (FileNotFoundException e) {
//
//        }
//        TGSong song = new FileImporter().importFile("/home/void/project/newtest-debug.xml");
//        new FileExporter().exportSong(song, "/home/void/project/newtest-debug.tg");
//        MusicStringParser parser = new MusicStringParser();
//        MusicXmlRenderer renderer = new MusicXmlRenderer();
//        parser.addParserListener(renderer);
//        parser.parse(new Pattern(ms.toString()));
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter("/home/void/project/newhouse-jfugue.xml"));
//            writer.write(renderer.getMusicXMLString());
//            writer.flush();
//        } catch (IOException e) {
//
//        }

//        System.out.println(ms.getAllTracks());
//        System.out.println(ms.getTrackIds());
//        String track1 = ms.getTrack("V15 GUITAR").toString();
//        String track2 = ms.getTrack("V4 STEEL_STRING_GUITAR").toString();
//        String track3 = ms.getTrack("V2 ELECTRIC_BASS_PICK").toString();
//        String track4 = ms.getTrack("V9 DRUMS").toString();
//        System.out.println(track1);
//        System.out.println(track2);
//        new Player().play(ms.toString());
/*        MusicString ms1 = new MusicString("/home/void/project/sun1.gp4", true);
        System.out.println(ms1.getAllTracks());*/
        //System.out.println(ms.getAllTracks());
        //System.out.println(ms.getTrackIds());
//        Player pl = new Player();
//        pl.play(ms.getTrack("V0 TENOR_SAX") + ms.getTrack("V4 STEEL_STRING_GUITAR"));
        //MusicString ms1 = new MusicString(ms.getAllTracks());
        //System.out.println(ms1.getAllTracks());
        //System.out.println(ms1.getTrackIds());
    }
}
