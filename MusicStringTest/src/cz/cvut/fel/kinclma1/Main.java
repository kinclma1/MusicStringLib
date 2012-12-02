
package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.io.FileExporter;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.musicxml.MusicXMLReader;
import org.herac.tuxguitar.io.tg.TGInputStream;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.*;
import org.jfugue.MusicStringParser;
import org.jfugue.MusicXmlRenderer;
import org.jfugue.Pattern;
import org.jfugue.Player;

import java.io.*;
import java.util.ArrayList;
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
//        MusicStringSong ms = new MusicStringSong(new File("/home/void/project/mayhem-short.tg"));
//        TGSong tgs = ms.toTGSong();
//        new FileExporter().exportSong(tgs, "/home/void/project/newmayhem-short.xml");

        try {
            new MusicXMLReader(new TGFactory(), new BufferedInputStream(new FileInputStream("/home/void/project/newmayhem-short.xml"))).readSong();
        } catch (FileNotFoundException e) {

        }

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
