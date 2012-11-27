package cz.cvut.fel.kinclma1.io;

import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 11.11.12
 * Time: 22:34
 * To change this template use File | Settings | File Templates.
 */
public class FileExporter {

    public void exportSong(TGSong song, String filename) {
        BufferedOutputStream out;
        String[] tmp = filename.split("\\.");
        String extension = tmp[tmp.length - 1];

        try {
            out = new BufferedOutputStream(new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
            System.err.println("File export failed, cannot write to file");
            return;
        }
        TGFileFormatManager formatManager = TGFileFormatManager.instance();
        Iterator<TGOutputStreamBase> outputStreams = formatManager.getOutputStreams();
        while (outputStreams.hasNext()) {
            TGOutputStreamBase outputStreamBase = outputStreams.next();
            if (outputStreamBase.getFileFormat().getSupportedFormats().contains(extension)) {
                outputStreamBase.init(new TGFactory(), out);
                try {
                    outputStreamBase.writeSong(song);
                } catch (IOException e) {
                    System.err.println("File export failed: " + e.getMessage());
                }
                return;
            }
        }
        Iterator<TGLocalFileExporter> exporters = formatManager.getExporters();
        while (exporters.hasNext()) {
            TGLocalFileExporter exporter = exporters.next();
            if (exporter.getFileFormat().getSupportedFormats().contains(extension)) {
                exporter.init(new TGFactory(), out);
                try {
                    exporter.exportSong(song);
                } catch (TGFileFormatException e) {
                    System.err.println("File export failed, file format does not work correctly");
                }
                return;
            }
        }
    }
}
