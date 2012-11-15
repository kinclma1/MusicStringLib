package cz.cvut.fel.kinclma1.io;

import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.io.tg.TGOutputStream;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 11.11.12
 * Time: 22:34
 * To change this template use File | Settings | File Templates.
 */
public class FileExporter {
    //todo different formats
    public void exportSong(TGSong song, String filename) {
        BufferedOutputStream out = null;

        try {
            out = new BufferedOutputStream(new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
            System.err.println("File export failed");
            return;
        }
        TGOutputStream tgos = new TGOutputStream();
        tgos.init(new TGFactory(), out);
        try {
            tgos.writeSong(song);
        } catch (IOException e) {
            System.err.println("File export failed");
        }
    }

    public void convert(TGSong song, String fileName) {
        try {
            TGSongManager manager = new TGSongManager();
            manager.setSong(song);

            manager.setSong(song);
            manager.autoCompleteSilences();
            manager.orderBeats();

            new File(new File(fileName).getParent()).mkdirs();

//            if (this.format != null && this.format.getExporter() instanceof TGOutputStreamBase) {
//                TGOutputStreamBase exporter = (TGOutputStreamBase) this.format.getExporter();
//                exporter.init(manager.getFactory(), new BufferedOutputStream(new FileOutputStream(convertFileName)));
//                exporter.writeSong(song);
//            } else if (this.format != null && this.format.getExporter() instanceof TGLocalFileExporter) {
//                TGLocalFileExporter exporter = (TGLocalFileExporter) this.format.getExporter();
//                exporter.configure(true);
//                exporter.init(manager.getFactory(), new BufferedOutputStream(new FileOutputStream(convertFileName)));
//                exporter.exportSong(manager.getSong());
//            }
        } catch (Exception e) {

        }
    }

    private boolean isSupportedExtension(String filename, TGLocalFileExporter currentExporter) {
        try {
            String extension = filename.substring(filename.lastIndexOf("."), filename.length());
            extension = "*" + extension.toLowerCase();
            String[] formats = currentExporter.getFileFormat().getSupportedFormats().split(";");
            for (int i = 0; i < formats.length; i++)
                if (formats[i].toLowerCase().equals(extension))
                    return true;
        } catch (Exception ex) {
            return false;
        }

        return false;
    }
}
